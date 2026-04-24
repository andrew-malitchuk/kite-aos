package presentation.core.ui.core.splash

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import kotlin.time.Duration.Companion.milliseconds

/**
 * Decorator that bridges the AndroidX SplashScreen API with Jetpack Compose content.
 *
 * AndroidX SplashScreen does not support Compose content natively. This decorator injects custom
 * Composables into the splash flow, enabling animated branding during app startup rather than
 * static images.
 *
 * @param activity the host [Activity] to install the splash screen on.
 * @param config the [SplashScreenConfig] that defines content and animation timing.
 *
 * @see SplashScreenConfig
 * @see SplashScreenConfigBuilder
 * @see splash
 *
 * @since 0.0.1
 */
// Suppressed: deeply nested builder DSL callbacks cause indentation violations in detekt
@Suppress("Indentation")
public class SplashScreenDecorator private constructor(
    activity: Activity,
    private val config: SplashScreenConfig,
) {
    public companion object {
        /**
         * Factory method for creating a [SplashScreenDecorator] with builder DSL configuration.
         *
         * @param activity the host [Activity] to install the splash screen on.
         * @param builder DSL block for configuring the [SplashScreenConfig].
         * @return a fully configured [SplashScreenDecorator] instance.
         *
         * @since 0.0.1
         */
        public fun create(activity: Activity, builder: SplashScreenConfigBuilder.() -> Unit): SplashScreenDecorator =
            SplashScreenDecorator(
                activity,
                SplashScreenConfigBuilder()
                    .apply(builder)
                    .build(),
            )
    }

    /**
     * Observable state that Composables use to know when to start their exit animations.
     *
     * @since 0.0.1
     */
    public val isVisible: MutableState<Boolean> = mutableStateOf(true)

    /**
     * Controls whether the splash screen should remain visible. Set to `false` when the app
     * is ready to proceed. AndroidX requires this callback to control dismissal timing.
     *
     * @since 0.0.1
     */
    public var shouldKeepOnScreen: Boolean = true

    init {
        activity.installSplashScreen().apply {
            setOnExitAnimationListener(::handleExitAnimation)
            setKeepOnScreenCondition { shouldKeepOnScreen }
        }
    }

    /** System splash ends → inject our Compose content for custom animations. */
    private fun handleExitAnimation(splashScreenViewProvider: SplashScreenViewProvider) {
        val systemSplashView = splashScreenViewProvider.view
        // Unsafe cast is safe: the splash screen view is always added to a ViewGroup by the system
        val parentViewGroup = systemSplashView.parent as ViewGroup

        // 1. Create the Compose View
        val composeView = createComposeView(splashScreenViewProvider, parentViewGroup)

        // 2. Add the view first
        parentViewGroup.addView(composeView)
    }

    /** Wraps user's Composable in a View because AndroidX works with View hierarchy. */
    private fun createComposeView(
        splashScreenViewProvider: SplashScreenViewProvider,
        parentViewGroup: ViewGroup,
    ): ComposeView = ComposeView(splashScreenViewProvider.view.context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)

        setContent {
            config.content(
                SplashScreenController(
                    decorator = this@SplashScreenDecorator,
                    onStartExitAnimation = {
                        // Suppressed: elevation values ensure the system splash view renders
                        // above other content during the crossfade exit animation
                        @Suppress("MagicNumber")
                        performExitAnimation(
                            systemSplashView =
                            splashScreenViewProvider.view.also {
                                it.translationZ = 10f
                                it.elevation = 10f
                            },
                            composeView = this@apply,
                            parentViewGroup = parentViewGroup,
                            splashScreenViewProvider = splashScreenViewProvider,
                        )
                    },
                ),
            )
        }
    }

    /**
     * Fades both system and custom views simultaneously.
     * Staggered timing prevents jarring visual jumps during transition.
     */
    private fun performExitAnimation(
        systemSplashView: View,
        composeView: ComposeView,
        parentViewGroup: ViewGroup,
        splashScreenViewProvider: SplashScreenViewProvider,
    ) {
        val baseDuration = config.exitAnimationDuration

        // Fade out the system splash screen
        ObjectAnimator.ofFloat(systemSplashView, View.ALPHA, 0f).apply {
            duration = baseDuration

            doOnEnd {
                parentViewGroup.removeView(systemSplashView)
            }
            start()
        }

        // Fade out the compose view with configurable offset
        ObjectAnimator.ofFloat(composeView, View.ALPHA, 0f).apply {
            duration = baseDuration + config.composeViewFadeDurationOffset
            doOnEnd {
                parentViewGroup.removeView(composeView)
                splashScreenViewProvider.remove()
            }
            start()
        }
    }

    /**
     * Signals Composables to start exit animations by setting [isVisible] to `false`.
     *
     * This only updates the state. The actual exit animation must be triggered by the
     * Composable content when ready.
     *
     * @since 0.0.1
     */
    public fun dismiss() {
        isVisible.value = false
    }
}

/**
 * Controller API for Composables to observe splash screen state and trigger exit animations.
 *
 * @param decorator the backing [SplashScreenDecorator] instance.
 * @param onStartExitAnimation callback to invoke when the Composable is ready to start the exit animation.
 *
 * @see SplashScreenDecorator
 *
 * @since 0.0.1
 */
public data class SplashScreenController(
    private val decorator: SplashScreenDecorator,
    private val onStartExitAnimation: () -> Unit,
) {
    /**
     * Observable state for triggering custom Compose animations when the splash should exit.
     *
     * @since 0.0.1
     */
    val isVisible: MutableState<Boolean> get() = decorator.isVisible

    /**
     * Call this when your Composable animations finish to trigger the platform exit animation.
     *
     * @since 0.0.1
     */
    public fun startExitAnimation(): Unit = onStartExitAnimation()
}

/**
 * Configuration for splash screen content and animation timing.
 *
 * @param content the Composable content to display during the splash screen.
 * @param exitAnimationDuration duration of the exit animation in milliseconds.
 * @param composeViewFadeDurationOffset additional duration offset for the Compose view fade-out
 *        relative to the system splash fade-out, in milliseconds.
 *
 * @since 0.0.1
 */
public data class SplashScreenConfig(
    val content: @Composable (SplashScreenController) -> Unit,
    val exitAnimationDuration: Long,
    val composeViewFadeDurationOffset: Long,
)

/**
 * DSL builder for constructing a [SplashScreenConfig].
 *
 * @see SplashScreenConfig
 * @see splash
 *
 * @since 0.0.1
 */
public class SplashScreenConfigBuilder {
    private var content: (@Composable (SplashScreenController) -> Unit)? = null

    /**
     * Default duration for exit animations in milliseconds.
     *
     * @since 0.0.1
     */
    public var exitAnimationDuration: Long = 600.milliseconds.inWholeMilliseconds

    /**
     * Additional duration for Compose view fade-out relative to the system splash, in milliseconds.
     *
     * @since 0.0.1
     */
    public var composeViewFadeDurationOffset: Long = 200.milliseconds.inWholeMilliseconds

    /**
     * Sets the custom Composable content to be displayed during the splash screen.
     *
     * @param block the Composable content block with [SplashScreenController] as receiver for
     *        accessing animation triggers.
     *
     * @since 0.0.1
     */
    public fun content(block: @Composable SplashScreenController.() -> Unit) {
        this.content = block
    }

    /** Validates and creates the final config. */
    internal fun build(): SplashScreenConfig {
        require(exitAnimationDuration >= 0) { "Exit animation duration must be positive" }
        require(composeViewFadeDurationOffset >= 0) { "Compose view fade duration offset cannot be negative" }
        return SplashScreenConfig(
            content = requireNotNull(content) { "Composable content must be provided for splash screen" },
            exitAnimationDuration = exitAnimationDuration,
            composeViewFadeDurationOffset = composeViewFadeDurationOffset,
        )
    }
}

/**
 * Extension function to create splash screens with custom Compose content via a DSL builder.
 *
 * ```kotlin
 * val exitAnimationDuration = 600L
 * val composeViewFadeDurationOffset = 200L
 * splashScreen = splash {
 *     this.exitAnimationDuration = exitAnimationDuration
 *     this.composeViewFadeDurationOffset = composeViewFadeDurationOffset
 *     content {
 *         HeartBeatAnimation(
 *             isVisible = isVisible.value,
 *             exitAnimationDuration = exitAnimationDuration.milliseconds,
 *             onStartExitAnimation = { startExitAnimation() }
 *         )
 *     }
 * }
 *
 * // Later, when ready to dismiss
 * splashScreen.shouldKeepOnScreen = false
 * splashScreen.dismiss()
 * ```
 *
 * @param builder DSL block for configuring the splash screen via [SplashScreenConfigBuilder].
 * @return a configured [SplashScreenDecorator] instance.
 *
 * @see SplashScreenDecorator
 * @see SplashScreenConfigBuilder
 *
 * @since 0.0.1
 */
public fun Activity.splash(builder: SplashScreenConfigBuilder.() -> Unit): SplashScreenDecorator =
    SplashScreenDecorator.create(this, builder)
