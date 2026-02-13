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
 * Reveal effect for changing theme which wrap-up content
 *
 * @param modifier
 * @param targetState
 * @param animationSpec
 * @param content
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
 * Reveal effect for changing theme
 *
 * @param modifier
 * @param animationSpec
 * @param content
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
    val contentMap = remember {
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
        val replacementId = currentlyVisible.indexOfFirst {
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
        modifier = modifier.pointerInteropFilter {
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

public fun Modifier.circularReveal(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    offset: Offset? = null,
): Modifier = clip(CircularRevealShape(progress, offset))

public class CircularRevealShape(
    @FloatRange(from = 0.0, to = 1.0) private val progress: Float,
    private val offset: Offset? = null,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
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