package presentation.core.ui.core.theme

import android.graphics.Path
import android.view.MotionEvent
import androidx.annotation.FloatRange
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.hypot

/**
 * A circular reveal transition composable that wraps content and animates state changes
 * with an expanding circle effect, typically used for theme switching.
 *
 * The reveal origin is determined by the user's last touch-down position, creating a natural
 * radial transition from the interaction point.
 *
 * @param T the type of the target state driving the transition.
 * @param modifier Modifier to be applied to the [Box].
 * @param targetState the target state that triggers a new reveal animation when changed.
 * @param animationSpec the [FiniteAnimationSpec] controlling the reveal animation timing.
 * @param content the composable content to display for each state value.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun <T> CircularReveal(
    modifier: Modifier = Modifier,
    targetState: T,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    content: @Composable (T) -> Unit,
) {
    val transition = updateTransition(targetState, label = "Circular reveal")
    transition.CircularReveal(modifier, animationSpec, content = content)
}

/**
 * A circular reveal transition composable that animates between states of this [Transition]
 * with an expanding circle clip effect.
 *
 * The reveal origin is captured from the user's touch-down event via [pointerInteropFilter].
 * When no touch is detected, the reveal expands from the center of the composable.
 *
 * @param T the type of the transition state.
 * @param modifier Modifier to be applied to the [Box].
 * @param animationSpec the [FiniteAnimationSpec] controlling the reveal animation timing.
 * @param content the composable content to display for each state value.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun <T> Transition<T>.CircularReveal(
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    content: @Composable (targetState: T) -> Unit,
) {
    var offset: Offset? by remember { mutableStateOf(null) }
    val currentlyVisible = remember { mutableStateListOf<T>().apply { add(currentState) } }
    val contentMap =
        remember {
            mutableMapOf<T, @Composable () -> Unit>()
        }
    if (currentState == targetState) {
        // If not animating, just display the current state
        if (currentlyVisible.size != 1 || currentlyVisible[0] != targetState) {
            // Remove all the intermediate items from the list once the animation is finished.
            currentlyVisible.removeAll { it != targetState }
            contentMap.clear()
        }
    }
    if (!contentMap.contains(targetState)) {
        // Replace target with the same key if any
        val replacementId =
            currentlyVisible.indexOfFirst {
                it == targetState
            }
        if (replacementId == -1) {
            currentlyVisible.add(targetState)
        } else {
            currentlyVisible[replacementId] = targetState
        }
        contentMap.clear()
        currentlyVisible.forEach { stateForContent ->
            contentMap[stateForContent] = {
                val progress by animateFloat(
                    label = "Progress",
                    transitionSpec = { animationSpec },
                ) {
                    val targetedContent =
                        stateForContent != currentlyVisible.last() || it == stateForContent
                    if (targetedContent) 1f else 0f
                }
                Box(Modifier.circularReveal(progress = progress, offset = offset)) {
                    content(stateForContent)
                }
            }
        }
    }
    Box(
        modifier =
        modifier.pointerInteropFilter {
            if (it.action == MotionEvent.ACTION_DOWN) {
                if (!started) offset = Offset(it.x, it.y)
            }
            started
        },
    ) {
        currentlyVisible.forEach {
            key(it) {
                contentMap[it]?.invoke()
            }
        }
    }
}

private val <T> Transition<T>.started
    get() =
        currentState != targetState || isRunning

/**
 * Clips the composable using a [CircularRevealShape] based on the given [progress] and optional
 * [offset] origin.
 *
 * @param progress the reveal progress from 0.0 (fully hidden) to 1.0 (fully revealed).
 * @param offset the optional origin point of the reveal circle. If null, the center of the
 *        composable is used.
 * @return a [Modifier] with the circular reveal clip applied.
 *
 * @see CircularRevealShape
 *
 * @since 0.0.1
 */
public fun Modifier.circularReveal(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    offset: Offset? = null,
): Modifier = clip(CircularRevealShape(progress, offset))

/**
 * A [Shape] that creates a circular outline for use with circular reveal animations.
 *
 * The circle radius is calculated as the longest distance from the [offset] origin to any
 * corner of the bounding rectangle, scaled by [progress]. This ensures the circle fully
 * covers the content at progress 1.0.
 *
 * @param progress the reveal progress from 0.0 (no circle) to 1.0 (fully revealed).
 * @param offset the optional origin point of the circle. If null, the center is used.
 *
 * @since 0.0.1
 */
public class CircularRevealShape(
    @FloatRange(from = 0.0, to = 1.0) private val progress: Float,
    private val offset: Offset? = null,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            Path().apply {
                addCircle(
                    offset?.x ?: (size.width / 2f),
                    offset?.y ?: (size.height / 2f),
                    longestDistanceToACorner(size, offset) * progress,
                    Path.Direction.CW,
                )
            }.asComposePath(),
        )
    }

    private fun longestDistanceToACorner(size: Size, offset: Offset?): Float {
        if (offset == null) {
            return hypot(size.width / 2f, size.height / 2f)
        }

        val topLeft = hypot(offset.x, offset.y)
        val topRight = hypot(size.width - offset.x, offset.y)
        val bottomLeft = hypot(offset.x, size.height - offset.y)
        val bottomRight = hypot(size.width - offset.x, size.height - offset.y)

        return topLeft.coerceAtLeast(topRight).coerceAtLeast(bottomLeft).coerceAtLeast(bottomRight)
    }
}
