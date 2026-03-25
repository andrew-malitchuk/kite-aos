package presentation.core.ui.core.configuration

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import presentation.core.ui.core.configuration.AnimationConfiguration.Duration.DEFAULT

/**
 * Configuration object for animation settings.
 *
 * This object contains nested objects and constants that define various animation-related configurations,
 * such as duration.
 */
public object AnimationConfiguration {
    /**
     * Object holding constants for animation durations.
     */
    public object Duration {
        /**
         * The short animation duration in milliseconds.
         */
        public const val SHORT: Int = 150

        /**
         * The default animation duration in milliseconds.
         */
        public const val DEFAULT: Int = 500

        /**
         * The default animation duration for snackbar in milliseconds.
         */
        public const val SNACKBAR: Long = 5_000L
    }

    /**
     * Object holding functions for animation transitions.
     */
    public object Transition {
        /**
         * Returns a default transition that combines a fade-in and fade-out animation with the default duration.
         *
         * @return A lambda function defining the content transform for the transition.
         */
        public fun <T> default(): AnimatedContentTransitionScope<T>.() -> ContentTransform = {
            fadeIn(
                animationSpec = tween(DEFAULT),
            ) togetherWith
                fadeOut(
                    animationSpec = tween(DEFAULT),
                )
        }
    }
}
