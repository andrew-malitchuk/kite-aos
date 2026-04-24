package presentation.core.ui.source.kit.atom.snackbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import presentation.core.ui.source.kit.atom.snackbar.Constant.TWEEN_ANIMATION_DURATION

/**
 * Animation presets for the stacked snackbar enter/exit transitions and stacking behavior.
 *
 * Each preset defines animation specs for padding between stacked items, scale reduction,
 * and enter/exit slide animations.
 *
 * @param paddingAnimationSpec animation spec for the bottom padding between stacked snackbars.
 * @param scaleAnimationSpec animation spec for the scale reduction of background snackbars.
 * @param enterAnimationSpec animation spec for the slide-in enter transition.
 * @param exitAnimationSpec animation spec for the slide-out exit transition.
 *
 * @since 0.0.1
 */
@Stable
public enum class StackedSnackbarAnimation(
    public val paddingAnimationSpec: AnimationSpec<Dp>,
    public val scaleAnimationSpec: AnimationSpec<Float>,
    public val enterAnimationSpec: FiniteAnimationSpec<IntOffset>,
    public val exitAnimationSpec: FiniteAnimationSpec<IntOffset>,
) {
    Bounce(
        paddingAnimationSpec =
        spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioHighBouncy,
        ),
        scaleAnimationSpec =
        spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioHighBouncy,
        ),
        enterAnimationSpec =
        spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioHighBouncy,
            visibilityThreshold = IntOffset.VisibilityThreshold,
        ),
        exitAnimationSpec =
        spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioHighBouncy,
            visibilityThreshold = IntOffset.VisibilityThreshold,
        ),
    ),
    Slide(
        paddingAnimationSpec =
        tween(
            TWEEN_ANIMATION_DURATION,
            0,
            FastOutSlowInEasing,
        ),
        scaleAnimationSpec =
        tween(
            TWEEN_ANIMATION_DURATION,
            0,
            FastOutSlowInEasing,
        ),
        enterAnimationSpec =
        tween(
            TWEEN_ANIMATION_DURATION,
            0,
            FastOutSlowInEasing,
        ),
        exitAnimationSpec =
        tween(
            TWEEN_ANIMATION_DURATION,
            0,
            FastOutSlowInEasing,
        ),
    ),
}

/**
 * Duration options for how long a stacked snackbar remains visible before auto-dismissal.
 *
 * @since 0.0.1
 */
@Stable
public enum class StackedSnackbarDuration {
    /** Display for a short period (4 seconds). */
    Short,

    /** Display for a longer period (10 seconds). */
    Long,

    /** Display indefinitely until manually dismissed. */
    Indefinite,
}

/**
 * Internal constants for stacked snackbar layout and animation thresholds.
 */
internal object Constant {
    /** Scale reduction per stacked snackbar level (5% smaller per level). */
    const val SCALE_DECREMENT = 0.05f
    /** Bottom padding increment in dp per stacked snackbar level. */
    const val PADDING_INCREMENT = 16
    /** Vertical offset (in px) for the enter slide-in animation origin. */
    const val Y_TARGET_ENTER = 100
    /** Vertical offset (in px) for the exit slide-out animation target. */
    const val Y_TARGET_EXIT = 750
    /** Horizontal offset (in px) for the rightward swipe-exit animation target. */
    const val X_TARGET_EXIT_RIGHT = 1000
    /** Horizontal offset (in px) for the leftward swipe-exit animation target. */
    const val X_TARGET_EXIT_LEFT = -1000
    /** Horizontal drag threshold (in px) to trigger a leftward swipe-exit. */
    const val OFFSET_THRESHOLD_EXIT_LEFT = -350
    /** Horizontal drag threshold (in px) to trigger a rightward swipe-exit. */
    const val OFFSET_THRESHOLD_EXIT_RIGHT = 350
    /** Duration in milliseconds for tween-based slide animations. */
    const val TWEEN_ANIMATION_DURATION = 100
}
