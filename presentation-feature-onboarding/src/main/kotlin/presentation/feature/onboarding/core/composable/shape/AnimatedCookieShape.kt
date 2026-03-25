package presentation.feature.onboarding.core.composable.shape

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
 * A decorative animated shape that morphs between different star polygons.
 *
 * This component uses the 'androidx.graphics.shapes' library to create a dynamic,
 * rotating background element. It continuously morphs its vertex count and
 * rotates to provide a modern, "alive" visual feel to the onboarding screens.
 *
 * @param modifier The modifier to be applied to the shape container.
 * @param color The color of the shape.
 */
@Composable
public fun AnimatedCookieShape(modifier: Modifier = Modifier, color: Color) {
    val shape12 =
        remember {
            RoundedPolygon.star(
                numVerticesPerRadius = VERTICES_LARGE,
                innerRadius = INNER_RADIUS,
                rounding = CornerRounding(ROUNDING),
                innerRounding = CornerRounding(ROUNDING),
            )
        }
    val shape9 =
        remember {
            RoundedPolygon.star(
                numVerticesPerRadius = VERTICES_MEDIUM,
                innerRadius = INNER_RADIUS,
                rounding = CornerRounding(ROUNDING),
                innerRounding = CornerRounding(ROUNDING),
            )
        }
    val shape7 =
        remember {
            RoundedPolygon.star(
                numVerticesPerRadius = VERTICES_SMALL,
                innerRadius = INNER_RADIUS,
                rounding = CornerRounding(ROUNDING),
                innerRounding = CornerRounding(ROUNDING),
            )
        }

    val shapes = listOf(shape12, shape9, shape7)

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    // Progress for morphing between shapes
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = shapes.size.toFloat(),
        animationSpec =
        infiniteRepeatable(
            animation = tween(MORPH_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "morphProgress",
    )

    // Continuous rotation to make it less static
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(ROTATION_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    val currentIndex = progress.toInt() % shapes.size
    val nextIndex = (currentIndex + 1) % shapes.size
    val morphProgress = progress % 1f

    val morph =
        remember(currentIndex, nextIndex) {
            Morph(shapes[currentIndex], shapes[nextIndex])
        }

    Box(
        modifier =
        modifier
            .drawWithCache {
                val androidMatrix = android.graphics.Matrix()
                val commonPath = android.graphics.Path()

                onDrawBehind {
                    commonPath.reset()
                    morph.toPath(morphProgress, commonPath)

                    androidMatrix.reset()
                    // Scale the shape from unit radius to actual size
                    androidMatrix.postScale(size.width / 2f, size.height / 2f)
                    // Rotate around the origin (0,0) before translating to center
                    androidMatrix.postRotate(rotation)
                    // Move the shape to the center of the drawing area
                    androidMatrix.postTranslate(size.width / 2f, size.height / 2f)

                    commonPath.transform(androidMatrix)
                    drawPath(commonPath.asComposePath(), color = color)
                }
            },
    )
}

private const val VERTICES_LARGE = 12
private const val VERTICES_MEDIUM = 9
private const val VERTICES_SMALL = 7
private const val INNER_RADIUS = 0.7f
private const val ROUNDING = 0.15f
private const val MORPH_DURATION = 4000
private const val ROTATION_DURATION = 30_000
