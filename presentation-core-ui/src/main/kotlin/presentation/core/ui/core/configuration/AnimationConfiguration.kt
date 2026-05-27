package presentation.core.ui.core.configuration

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import presentation.core.ui.core.configuration.AnimationConfiguration.Duration.DEFAULT

/**
 * Configuration object for animation settings used across the application.
 *
 * This object contains nested objects and constants that define various animation-related configurations,
 * such as duration and transition specifications.
 *
 * @since 0.0.1
 */
public object AnimationConfiguration {
    /**
     * Object holding constants for animation durations in milliseconds.
     *
     * @since 0.0.1
     */
    public object Duration {
        /**
         * The short animation duration in milliseconds.
         *
         * @since 0.0.1
         */
        public const val SHORT: Int = 150

        /**
         * The default animation duration in milliseconds.
         *
         * @since 0.0.1
         */
        public const val DEFAULT: Int = 500

        /**
         * The default animation duration for snackbar in milliseconds.
         *
         * @since 0.0.1
         */
        public const val SNACKBAR: Long = 5_000L
    }

    /**
     * Object holding factory functions for common animation transitions.
     *
     * @since 0.0.1
     */
    public object Transition {
        /**
         * Returns a default transition that combines a fade-in and fade-out animation with the
         * [Duration.DEFAULT] duration.
         *
         * @param T the type of the animated content state.
         * @return A lambda function defining the content transform for the transition.
         *
         * @since 0.0.1
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
