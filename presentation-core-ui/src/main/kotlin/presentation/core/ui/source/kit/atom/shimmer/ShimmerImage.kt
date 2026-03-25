package presentation.core.ui.source.kit.atom.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import presentation.core.styling.core.Theme

/**
 * A composable that displays an image with a shimmer effect.
 *
 * @param imageVector The ImageVector to be displayed with the shimmer effect.
 * @param modifier The modifier to be applied to the composable.
 * @param baseColor The base color of the shimmer effect.
 * @param highlightColor The highlight color of the shimmer effect.
 * @param shimmerDuration The duration of the shimmer animation in milliseconds.
 */
@Suppress("MagicNumber")
@Composable
public fun ShimmerImage(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    baseColor: Color = Theme.color.inkSubtle,
    highlightColor: Color = Theme.color.outlineHigh,
    shimmerDuration: Int = 500,
) {
    val transition = rememberInfiniteTransition(label = "")
    val offsetX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1000f,
        animationSpec =
        infiniteRepeatable(
            tween(durationMillis = shimmerDuration, easing = LinearEasing),
            RepeatMode.Restart,
        ),
        label = "",
    )

    val brush =
        Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset(offsetX, 0f),
            end = Offset(offsetX + 500f, 500f),
        )

    val painter = rememberVectorPainter(imageVector)

    Canvas(modifier = modifier) {
        with(drawContext.canvas.nativeCanvas) {
            val checkpoint = saveLayer(null, null)
            drawIntoCanvas {
                with(painter) {
                    draw(size)
                }
            }
            drawRect(
                brush = brush,
                size = size,
                blendMode = BlendMode.SrcAtop,
            )
            restoreToCount(checkpoint)
        }
    }
}
