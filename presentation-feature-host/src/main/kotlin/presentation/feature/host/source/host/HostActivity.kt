package presentation.feature.host.source.host

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.animation.ValueAnimator
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.impl.source.host.NavigationHost
import presentation.core.platform.source.command.RemoteCommandBus
import presentation.core.platform.source.config.AppConfig
import presentation.core.platform.source.service.MqttService
import presentation.core.styling.core.FormFactor
import presentation.core.styling.core.LocalFormFactor
import presentation.core.styling.core.LocalWindowSizeClass
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.core.splash.SplashLoading
import presentation.core.ui.core.splash.SplashScreenDecorator
import presentation.core.ui.core.splash.splash

/**
 * The main and single activity of the application.
 *
 * This activity hosts the entire composable UI and manages the initial setup,
 * including the splash screen and determining the initial navigation route.
 * It enforces immersive (full-screen) mode for the kiosk experience and blocks
 * the system back gesture/button.
 *
 * @see HostViewModel
 * @see HostState
 * @see HostSideEffect
 * @since 0.0.1
 */
public class HostActivity : AppCompatActivity() {
    private var splashScreen: SplashScreenDecorator? = null
    private val viewModel: HostViewModel by inject()
    private val appConfig: AppConfig by inject()
    private val remoteCommandBus: RemoteCommandBus by inject()
    private var autoReturnJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only use modern edge-to-edge on Android 11+ (API 30).
        // On older versions, we let the system handle window resizing (adjustResize)
        // to avoid critical keyboard layout bugs.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            enableEdgeToEdge()
        }

        setupSplashScreen()
        setupContent()
    }

    override fun onStart() {
        super.onStart()
        ContextCompat.startForegroundService(this, Intent(this, MqttService::class.java))
    }

    override fun onResume() {
        super.onResume()
        autoReturnJob?.cancel()
        autoReturnJob = null
        hideSystemUI()
    }

    /**
     * Schedules an auto-return to the kiosk when the activity is fully hidden
     * (e.g., an external app was launched from the control drawer or MQTT).
     *
     * Uses [onStop] instead of [onPause] to avoid triggering for partial occlusion
     * (permission dialogs, system prompts). The delayed [Intent] brings [HostActivity]
     * back to the foreground via [Intent.FLAG_ACTIVITY_REORDER_TO_FRONT].
     *
     * Requires `SYSTEM_ALERT_WINDOW` permission for background activity starts on API 29+.
     */
    override fun onStop() {
        super.onStop()
        if (!viewModel.container.stateFlow.value.isAutoReturnEnabled) return
        autoReturnJob = lifecycleScope.launch {
            delay(AUTO_RETURN_DELAY_MS)
            val intent = Intent(this@HostActivity, HostActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        autoReturnJob?.cancel()
        splashScreen = null
        super.onDestroy()
    }

    // How many keys of [SETTINGS_UNLOCK_SEQUENCE] have been matched so far.
    private var unlockProgress = 0

    // event.eventTime (ms) of the last matched key. A pause longer than
    // [UNLOCK_SEQUENCE_TIMEOUT_MS] between keys resets the combo so a stale prefix
    // never lingers between unrelated navigation sessions.
    private var lastUnlockKeyTime = 0L

    /**
     * Intercepts the TV "settings unlock" key sequence before it reaches the focused WebView.
     *
     * On TV the control drawer (there is no FAB) is opened by entering a hidden Konami-style
     * D-pad sequence — see [SETTINGS_UNLOCK_SEQUENCE]. A plain key long-press was dropped because
     * many TV remotes lack a MENU key; a D-pad-only combo works on every remote. The sequence is
     * long and distinctive so ordinary dashboard navigation can't trigger it by accident.
     *
     * Intermediate keys are intentionally passed through so the dashboard still reacts to them;
     * only the key that completes the sequence is consumed (to open the drawer). On mobile this is
     * a pure pass-through, leaving touch behaviour unchanged.
     *
     * [dispatchKeyEvent] is used (rather than a Compose key modifier) because Compose key
     * modifiers are bypassed while the `AndroidView`-hosted WebView owns focus.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (!appConfig.isTv) return super.dispatchKeyEvent(event)

        // Count each physical press once (repeatCount == 0 skips auto-repeat while a key is held).
        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            if (advanceUnlockSequence(event.keyCode, event.eventTime)) {
                remoteCommandBus.emitOpenDrawer()
                unlockProgress = 0
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * Feeds one key press into the unlock-sequence matcher.
     *
     * @return `true` when [keyCode] completes the full [SETTINGS_UNLOCK_SEQUENCE].
     */
    private fun advanceUnlockSequence(keyCode: Int, eventTime: Long): Boolean {
        // Drop a partial combo if the user paused too long between keys.
        if (eventTime - lastUnlockKeyTime > UNLOCK_SEQUENCE_TIMEOUT_MS) {
            unlockProgress = 0
        }
        lastUnlockKeyTime = eventTime

        unlockProgress =
            when {
                keyCode == SETTINGS_UNLOCK_SEQUENCE[unlockProgress] -> unlockProgress + 1
                // A wrong key restarts the match, but still counts if it is itself the first key.
                keyCode == SETTINGS_UNLOCK_SEQUENCE[0] -> 1
                else -> 0
            }

        return unlockProgress == SETTINGS_UNLOCK_SEQUENCE.size
    }

    /**
     * Initializes the custom splash screen and configures its exit transition.
     * The splash screen is kept on screen manually until the bootstrapping
     * process in the ViewModel is complete.
     */
    private fun setupSplashScreen() {
        splashScreen =
            splash {
                content {
                    val state by viewModel.collectAsState()

                    exitAnimationDuration = SPLASH_EXIT_ANIMATION_DURATION
                    composeViewFadeDurationOffset = COMPOSE_FADE_DURATION_OFFSET

                    // Apply the theme to the splash screen content to match the app's look.
                    AppTheme(mode = state.theme) {
                        SplashLoading(
                            isVisible = isVisible.value,
                            exitAnimationDuration = SPLASH_EXIT_ANIMATION_DURATION.toInt(),
                            onStartExitAnimation = { startExitAnimation() },
                        )
                    }
                }
            }
        // Keep the splash screen visible until we explicitly hide it.
        splashScreen?.shouldKeepOnScreen = true
    }

    /**
     * Sets up the Jetpack Compose content for the activity.
     * Observes the [HostViewModel] state for theme updates and navigation
     * destination, and handles the [HostSideEffect.DismissSplashEffect]
     * to transition from the splash screen to the main UI.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    private fun setupContent() {
        setContent {
            val state by viewModel.collectAsState()

            // Resolve the form-factor + window size class once at the host root and
            // publish them above AppTheme so 10-foot scaling and TV input branches
            // work everywhere without prop-drilling. On mobile these default to
            // MOBILE / a compact-ish class, so behaviour is unchanged.
            val formFactor = if (appConfig.isTv) FormFactor.TV else FormFactor.MOBILE
            val windowSizeClass = calculateWindowSizeClass(this@HostActivity)

            // Collect side effects to handle UI-only transitions.
            viewModel.collectSideEffect { effect ->
                when (effect) {
                    HostSideEffect.DismissSplashEffect -> {
                        lifecycleScope.launch {
                            // Allow the splash screen to be dismissed now that the
                            // start destination is determined.
                            splashScreen?.shouldKeepOnScreen = false
                            // Add a small delay for a smoother visual transition.
                            delay(SPLASH_DISMISS_DELAY)
                            splashScreen?.dismiss()
                        }
                    }
                }
            }

            LaunchedEffect(state.isReduceMotionEnabled) {
                val scale = if (state.isReduceMotionEnabled) 0f else 1f
                runCatching {
                    ValueAnimator::class.java
                        .getMethod("setDurationScale", Float::class.javaPrimitiveType)
                        .invoke(null, scale)
                }
            }

            CompositionLocalProvider(
                LocalFormFactor provides formFactor,
                LocalWindowSizeClass provides windowSizeClass,
            ) {
                AppTheme(mode = state.theme) {
                    // Block the back gesture/button globally to maintain kiosk integrity.
                    BackHandler(enabled = true) {
                        Log.d("HostActivity", "Back gesture/button blocked.")
                    }

                    // The NavigationHost is composed once the start destination is decided.
                    val currentDestination = state.startDestination
                    if (currentDestination != null) {
                        NavigationHost(startDestination = currentDestination)
                    }
                }
            }
        }
    }

    /**
     * Re-enforces immersive mode whenever the window regains focus.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    /**
     * Hides the system status and navigation bars to provide a full-screen
     * kiosk experience. Uses transient bar behavior for temporary access.
     */
    private fun hideSystemUI() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private companion object Companion {
        /** The duration for the splash screen exit animation in milliseconds. */
        const val SPLASH_EXIT_ANIMATION_DURATION = 800L

        /** The offset for the Compose view fade-in duration relative to the splash screen exit. */
        const val COMPOSE_FADE_DURATION_OFFSET = 200L

        /** The artificial delay before dismissing the splash screen after the destination is decided. */
        const val SPLASH_DISMISS_DELAY = 1500L

        /** Delay before auto-returning to the kiosk after the activity is stopped. */
        const val AUTO_RETURN_DELAY_MS = 30_000L

        /**
         * Hidden D-pad "Konami" sequence that opens the control drawer on TV. Uses only
         * directional keys so it works on every remote (unlike MENU, which many TV remotes
         * lack). Kept long and distinctive so ordinary dashboard navigation can't complete it
         * by accident. Single constant so the combo is trivial to retune: Up, Up, Down, Down,
         * Left, Right, Left, Right.
         */
        val SETTINGS_UNLOCK_SEQUENCE =
            intArrayOf(
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT,
            )

        /** Max pause (ms) allowed between two keys before the unlock combo resets. */
        const val UNLOCK_SEQUENCE_TIMEOUT_MS = 3_000L
    }
}
