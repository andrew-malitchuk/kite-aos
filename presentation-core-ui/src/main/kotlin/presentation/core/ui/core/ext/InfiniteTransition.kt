package presentation.core.ui.core.ext

import androidx.annotation.IntRange
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Creates a fractioned infinite float animation that interpolates through evenly spaced keyframes.
 *
 * The [fraction] parameter controls how many intermediate keyframes are inserted between
 * [initialValue] and [targetValue]. For example, a fraction of 3 creates keyframes at 1/3,
 * 2/3, and the full target value, each evenly distributed across [durationMillis].
 *
 * @param initialValue the starting value of the animation.
 * @param targetValue the ending value of the animation.
 * @param fraction the number of evenly spaced keyframe segments (1 to 4).
 * @param durationMillis total duration of one animation cycle in milliseconds.
 * @param delayMillis delay before the keyframe animation starts, in milliseconds.
 * @param offsetMillis initial start offset for the infinite repetition, in milliseconds.
 * @param repeatMode the [RepeatMode] for the infinite animation.
 * @param easing the [Easing] curve applied to each keyframe segment.
 * @return a [State] holding the current animated float value.
 *
 * @since 0.0.1
 */
@Composable
internal fun InfiniteTransition.fractionTransition(
    initialValue: Float,
    targetValue: Float,
    @IntRange(from = 1, to = 4) fraction: Int = 1,
    durationMillis: Int,
    delayMillis: Int = 0,
    offsetMillis: Int = 0,
    repeatMode: RepeatMode = RepeatMode.Restart,
    easing: Easing = FastOutSlowInEasing,
): State<Float> {
    return animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        label = "transition",
        animationSpec =
        infiniteRepeatable(
            animation =
            keyframes {
                this.durationMillis = durationMillis
                this.delayMillis = delayMillis
                initialValue at 0 with easing

                // Insert evenly spaced keyframes based on the fraction count:
                // e.g., fraction=3 creates keyframes at 1/3, 2/3, and 3/3 of the total
                // target value and duration
                when (fraction) {
                    1 -> {
                        targetValue at durationMillis with easing
                    }

                    2 -> {
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue at durationMillis with easing
                    }

                    3 -> {
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue / fraction * 2 at durationMillis / fraction * 2 with easing
                        targetValue at durationMillis with easing
                    }

                    4 -> {
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue / fraction * 2 at durationMillis / fraction * 2 with easing
                        targetValue / fraction * 3 at durationMillis / fraction * 3 with easing
                        targetValue at durationMillis with easing
                    }
                }
            },
            repeatMode = repeatMode,
            initialStartOffset = StartOffset(offsetMillis),
        ),
    )
}
