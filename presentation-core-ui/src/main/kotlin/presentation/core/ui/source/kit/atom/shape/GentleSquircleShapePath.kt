/*
 * Copyright (c) 2020-2025 MartinRGB, Sylvain BARRÉ-PERSYN, Stoyan Vuchev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package presentation.core.ui.source.kit.atom.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.LayoutDirection
import presentation.core.ui.source.kit.atom.shape.util.clampedCornerRadius
import presentation.core.ui.source.kit.atom.shape.util.clampedSmoothing

/**
 * Builds a [Path] for drawing a Gentle Squircle shape that gently morphs from a squircle
 * into a circle when applying larger corner radii.
 *
 * The algorithm works as follows for each corner:
 * 1. The raw corner radius is scaled by a 1.2x multiplier to match the visual size of
 *    standard rounded corners, then clamped to half the smallest dimension.
 * 2. If the clamped radius reaches or exceeds the corner threshold (half of [Size.minDimension]),
 *    a circular arc is used instead of a cubic Bezier curve.
 * 3. Otherwise, a cubic Bezier is drawn with a smoothing factor computed by [clampedSmoothing],
 *    which gradually reduces the squircle effect as the radius approaches the threshold.
 *
 * @param size The size of the shape in pixels.
 * @param topLeftCorner The top left corner radius in pixels.
 * @param topRightCorner The top right corner radius in pixels.
 * @param bottomLeftCorner The bottom left corner radius in pixels.
 * @param bottomRightCorner The bottom right corner radius in pixels.
 * @return A closed [Path] representing the gentle squircle outline.
 *
 * @see squircleShapePath
 * @see clampedCornerRadius
 * @see clampedSmoothing
 * @since 0.0.1
 **/
@Suppress("LongMethod")
public fun gentleSquircleShapePath(
    size: Size,
    topLeftCorner: Float,
    topRightCorner: Float,
    bottomLeftCorner: Float,
    bottomRightCorner: Float,
): Path {
    val width = size.width
    val height = size.height
    val minDimension = size.minDimension
    // The threshold at which corners switch from cubic Bezier to circular arcs.
    val cornerThreshold = minDimension / 2

    // Scale each corner radius by 1.2x to visually match the size of standard rounded corners,
    // then clamp to prevent overflow beyond half the smallest axis.
    val topLeft = clampedCornerRadius(topLeftCorner * 1.2f, size)
    val topRight = clampedCornerRadius(topRightCorner * 1.2f, size)
    val bottomRight = clampedCornerRadius(bottomRightCorner * 1.2f, size)
    val bottomLeft = clampedCornerRadius(bottomLeftCorner * 1.2f, size)

    // When the radius reaches the threshold, switch to a true circular arc for that corner.
    val useArcAtTopLeft = topLeft >= cornerThreshold
    val useArcAtTopRight = topRight >= cornerThreshold
    val useArcAtBottomRight = bottomRight >= cornerThreshold
    val useArcAtBottomLeft = bottomLeft >= cornerThreshold

    // Compute per-corner smoothing factors that control the cubic Bezier control point offset.
    // The factor gradually reduces the squircle effect as radius approaches the threshold,
    // producing the "gentle" morphing behavior.
    val topLeftSmoothingFactor = clampedSmoothing(topLeft, cornerThreshold)
    val topRightSmoothingFactor = clampedSmoothing(topRight, cornerThreshold)
    val bottomRightSmoothingFactor = clampedSmoothing(bottomRight, cornerThreshold)
    val bottomLeftSmoothingFactor = clampedSmoothing(bottomLeft, cornerThreshold)

    return Path().apply {
        // Enter the path at x = topLeft & y = 0.

        moveTo(
            x = topLeft,
            y = 0f,
        )

        // Top-right corner.

        lineTo(
            x = width - topRight,
            y = 0f,
        )

        if (useArcAtTopRight) {
            arcTo(
                rect =
                Rect(
                    left = width - 2 * topRight,
                    top = 0f,
                    right = width,
                    bottom = 2 * topRight,
                ),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
        } else {
            cubicTo(
                x1 = width - topRight * topRightSmoothingFactor,
                y1 = 0f,
                x2 = width,
                y2 = topRight * topRightSmoothingFactor,
                x3 = width,
                y3 = topRight,
            )
        }

        // Bottom-right corner.

        lineTo(
            x = width,
            y = height - bottomRight,
        )

        if (useArcAtBottomRight) {
            arcTo(
                rect =
                Rect(
                    left = width - 2 * bottomRight,
                    top = height - 2 * bottomRight,
                    right = width,
                    bottom = height,
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
        } else {
            cubicTo(
                x1 = width,
                y1 = height - bottomRight * bottomRightSmoothingFactor,
                x2 = width - bottomRight * bottomRightSmoothingFactor,
                y2 = height,
                x3 = width - bottomRight,
                y3 = height,
            )
        }

        // Bottom-left corner.

        lineTo(
            x = bottomLeft,
            y = height,
        )

        if (useArcAtBottomLeft) {
            arcTo(
                rect =
                Rect(
                    left = 0f,
                    top = height - 2 * bottomLeft,
                    right = 2 * bottomLeft,
                    bottom = height,
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
        } else {
            cubicTo(
                x1 = bottomLeft * bottomLeftSmoothingFactor,
                y1 = height,
                x2 = 0f,
                y2 = height - bottomLeft * bottomLeftSmoothingFactor,
                x3 = 0f,
                y3 = height - bottomLeft,
            )
        }

        // Top-left corner.

        lineTo(
            x = 0f,
            y = topLeft,
        )

        if (useArcAtTopLeft) {
            arcTo(
                rect =
                Rect(
                    left = 0f,
                    top = 0f,
                    right = 2 * topLeft,
                    bottom = 2 * topLeft,
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
        } else {
            cubicTo(
                x1 = 0f,
                y1 = topLeft * topLeftSmoothingFactor,
                x2 = topLeft * topLeftSmoothingFactor,
                y2 = 0f,
                x3 = topLeft,
                y3 = 0f,
            )
        }

        // Close the path.

        close()
    }
}

/**
 * Draws a Gentle Squircle with the given [Color]. Whether the Gentle Squircle is
 * filled or stroked (or both) is controlled by [Paint.style].
 *
 * Corner radii are automatically swapped for right-to-left layout directions.
 *
 * @param color The color to be applied to the Squircle.
 * @param topLeft Offset from the local origin of 0, 0 relative to the current translation.
 * @param size Dimensions of the Squircle to draw.
 * @param topLeftCorner The top left corner radius in pixels.
 * @param topRightCorner The top right corner radius in pixels.
 * @param bottomLeftCorner The bottom left corner radius in pixels.
 * @param bottomRightCorner The bottom right corner radius in pixels.
 * @param style Specifies whether the Squircle is stroked or filled in.
 * @param alpha Opacity to be applied to Squircle from 0.0f to 1.0f representing
 *   fully transparent to fully opaque respectively.
 * @param colorFilter [ColorFilter] to apply to the [color] when drawn into the destination.
 * @param blendMode Blending algorithm to be applied to the color.
 *
 * @see gentleSquircleShapePath
 * @see drawSquircle
 * @since 0.0.1
 */
public fun DrawScope.drawGentleSquircle(
    color: Color,
    topLeft: Offset = Offset.Zero,
    size: Size,
    topLeftCorner: Float,
    topRightCorner: Float,
    bottomLeftCorner: Float,
    bottomRightCorner: Float,
    style: DrawStyle = Fill,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode,
) {
    // Mirror corners horizontally for right-to-left layouts.
    val isRtl = this.layoutDirection == LayoutDirection.Rtl
    val path =
        gentleSquircleShapePath(
            size = size,
            topLeftCorner = if (isRtl) topLeftCorner else topRightCorner,
            topRightCorner = if (isRtl) topRightCorner else topLeftCorner,
            bottomLeftCorner = if (isRtl) bottomLeftCorner else bottomRightCorner,
            bottomRightCorner = if (isRtl) bottomRightCorner else bottomLeftCorner,
        )

    // Translate the canvas to the requested offset before drawing the path.
    translate(
        left = topLeft.x,
        top = topLeft.y,
    ) {
        drawPath(
            path = path,
            color = color,
            alpha = alpha,
            style = style,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}

/**
 * Draws a Gentle Squircle with the given [Brush]. Whether the Gentle Squircle is
 * filled or stroked (or both) is controlled by [Paint.style].
 *
 * Corner radii are automatically swapped for right-to-left layout directions.
 *
 * @param brush The brush (gradient or shader) to be applied to the Squircle.
 * @param topLeft Offset from the local origin of 0, 0 relative to the current translation.
 * @param size Dimensions of the Squircle to draw.
 * @param topLeftCorner The top left corner radius in pixels.
 * @param topRightCorner The top right corner radius in pixels.
 * @param bottomLeftCorner The bottom left corner radius in pixels.
 * @param bottomRightCorner The bottom right corner radius in pixels.
 * @param style Specifies whether the Squircle is stroked or filled in.
 * @param alpha Opacity to be applied to Squircle from 0.0f to 1.0f representing
 *   fully transparent to fully opaque respectively.
 * @param colorFilter [ColorFilter] to apply to the [brush] when drawn into the destination.
 * @param blendMode Blending algorithm to be applied to the brush.
 *
 * @see gentleSquircleShapePath
 * @see drawSquircle
 * @since 0.0.1
 */
public fun DrawScope.drawGentleSquircle(
    brush: Brush,
    topLeft: Offset = Offset.Zero,
    size: Size,
    topLeftCorner: Float,
    topRightCorner: Float,
    bottomLeftCorner: Float,
    bottomRightCorner: Float,
    style: DrawStyle = Fill,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode,
) {
    // Mirror corners horizontally for right-to-left layouts.
    val isRtl = this.layoutDirection == LayoutDirection.Rtl
    val path =
        gentleSquircleShapePath(
            size = size,
            topLeftCorner = if (isRtl) topLeftCorner else topRightCorner,
            topRightCorner = if (isRtl) topRightCorner else topLeftCorner,
            bottomLeftCorner = if (isRtl) bottomLeftCorner else bottomRightCorner,
            bottomRightCorner = if (isRtl) bottomRightCorner else bottomLeftCorner,
        )

    // Translate the canvas to the requested offset before drawing the path.
    translate(
        left = topLeft.x,
        top = topLeft.y,
    ) {
        drawPath(
            path = path,
            brush = brush,
            alpha = alpha,
            style = style,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}
