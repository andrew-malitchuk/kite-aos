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

@Stable
public enum class StackedSnackbarDuration {
    Short,
    Long,
    Indefinite,
}

internal object Constant {
    const val SCALE_DECREMENT = 0.05f
    const val PADDING_INCREMENT = 16
    const val Y_TARGET_ENTER = 100
    const val Y_TARGET_EXIT = 750
    const val X_TARGET_EXIT_RIGHT = 1000
    const val X_TARGET_EXIT_LEFT = -1000
    const val OFFSET_THRESHOLD_EXIT_LEFT = -350
    const val OFFSET_THRESHOLD_EXIT_RIGHT = 350
    const val TWEEN_ANIMATION_DURATION = 100
}
