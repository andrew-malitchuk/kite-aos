package presentation.feature.host.source.host

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.impl.source.host.NavigationHost
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.core.splash.SplashLoading
import presentation.core.ui.core.splash.SplashScreenDecorator
import presentation.core.ui.core.splash.splash

/**
 * The main and single activity of the application.
 * This activity hosts the entire composable UI and manages the initial setup,
 * including the splash screen and determining the initial navigation route.
 */
public class HostActivity : AppCompatActivity() {

    private var splashScreen: SplashScreenDecorator? = null
    private val viewModel: HostViewModel by inject()

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

    override fun onDestroy() {
        splashScreen = null // Avoid memory leaks when the activity is destroyed.
        super.onDestroy()
    }

    /**
     * Initializes the custom splash screen and configures its exit transition.
     * The splash screen is kept on screen manually until the bootstrapping
     * process in the ViewModel is complete.
     */
    private fun setupSplashScreen() {
        splashScreen = splash {
            content {
                val state by viewModel.collectAsState()

                exitAnimationDuration = SPLASH_EXIT_ANIMATION_DURATION
                composeViewFadeDurationOffset = COMPOSE_FADE_DURATION_OFFSET

                // Apply the theme to the splash screen content to match the app's look.
                AppTheme(mode = state.theme) {
                    SplashLoading(
                        isVisible = isVisible.value,
                        exitAnimationDuration = SPLASH_EXIT_ANIMATION_DURATION.toInt(),
                        onStartExitAnimation = { startExitAnimation() }
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
    private fun setupContent() {
        setContent {
            val state by viewModel.collectAsState()

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
    }
}
