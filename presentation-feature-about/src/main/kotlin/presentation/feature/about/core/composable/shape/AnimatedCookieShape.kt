package presentation.feature.about.core.composable.shape

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

/**
 * A custom Jetpack Compose component that draws a morphing star-like shape.
 *
 * This Composable uses the Android Graphics Shapes library to animate a [Morph]
 * between multiple [RoundedPolygon] star shapes, while simultaneously rotating the
 * result. It's used as a dynamic background for the application icon on the About screen.
 *
 * @param modifier Modifier to be applied to the box containing the shape.
 * @param color The color used to draw the morphing shape.
 */
@Composable
public fun AnimatedCookieShape(
    modifier: Modifier = Modifier,
    color: Color,
) {
    val shape12 = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 12,
            innerRadius = 0.7f,
            rounding = CornerRounding(0.15f),
            innerRounding = CornerRounding(0.15f)
        )
    }
    val shape9 = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 9,
            innerRadius = 0.7f,
            rounding = CornerRounding(0.15f),
            innerRounding = CornerRounding(0.15f)
        )
    }
    val shape7 = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 7,
            innerRadius = 0.7f,
            rounding = CornerRounding(0.15f),
            innerRounding = CornerRounding(0.15f)
        )
    }

    val shapes = listOf(shape12, shape9, shape7)

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = shapes.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "morphProgress"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val currentIndex = progress.toInt() % shapes.size
    val nextIndex = (currentIndex + 1) % shapes.size
    val morphProgress = progress % 1f

    val morph = remember(currentIndex, nextIndex) {
        Morph(shapes[currentIndex], shapes[nextIndex])
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val androidMatrix = android.graphics.Matrix()
                val commonPath = android.graphics.Path()

                onDrawBehind {
                    commonPath.reset()
                    morph.toPath(morphProgress, commonPath)

                    androidMatrix.reset()
                    androidMatrix.postScale(size.width / 2f, size.height / 2f)
                    androidMatrix.postRotate(rotation)
                    androidMatrix.postTranslate(size.width / 2f, size.height / 2f)

                    commonPath.transform(androidMatrix)
                    drawPath(commonPath.asComposePath(), color =color)
                }
            }
    )
}
