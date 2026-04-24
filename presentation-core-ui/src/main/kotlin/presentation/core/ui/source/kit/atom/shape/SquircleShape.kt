/*
 * Copyright (c) 2023-2025 Stoyan Vuchev
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

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import presentation.core.ui.source.kit.atom.shape.util.createSquircleShapeOutline

/**
 * Creates a [SquircleShape] with corner radius percentage defined as an [Int] value.
 *
 * All four corners share the same radius percentage and smoothing.
 *
 * @param percent The corner radius percentage from 0 to 100.
 * @param smoothing The smoothing factor from 0 to 100, defaults to [CornerSmoothing.Medium].
 * @return A new [SquircleShape] with uniform corner radii.
 *
 * @see SquircleShape
 * @see SquircleBasedShape
 * @see CornerSmoothing
 * @since 0.0.1
 **/
public fun SquircleShape(percent: Int = 100, smoothing: Int = CornerSmoothing.Medium): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(percent),
    topEndCorner = CornerSize(percent),
    bottomStartCorner = CornerSize(percent),
    bottomEndCorner = CornerSize(percent),
    smoothing = smoothing,
)

/**
 * Creates a [SquircleShape] with corner radius defined as a [Dp] value.
 *
 * All four corners share the same radius and smoothing.
 *
 * @param radius The corner radius in density-independent pixels.
 * @param smoothing The smoothing factor from 0 to 100, defaults to [CornerSmoothing.Medium].
 * @return A new [SquircleShape] with uniform corner radii.
 *
 * @see SquircleShape
 * @see CornerSmoothing
 * @since 0.0.1
 **/
public fun SquircleShape(radius: Dp, smoothing: Int = CornerSmoothing.Medium): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(radius),
    topEndCorner = CornerSize(radius),
    bottomStartCorner = CornerSize(radius),
    bottomEndCorner = CornerSize(radius),
    smoothing = smoothing,
)

/**
 * Creates a [SquircleShape] with corner radius in pixels defined as a [Float] value.
 *
 * All four corners share the same radius and smoothing.
 *
 * @param radius The corner radius in pixels.
 * @param smoothing The smoothing factor from 0 to 100, defaults to [CornerSmoothing.Medium].
 * @return A new [SquircleShape] with uniform corner radii.
 *
 * @see SquircleShape
 * @see CornerSmoothing
 * @since 0.0.1
 **/
public fun SquircleShape(radius: Float, smoothing: Int = CornerSmoothing.Medium): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(radius),
    topEndCorner = CornerSize(radius),
    bottomStartCorner = CornerSize(radius),
    bottomEndCorner = CornerSize(radius),
    smoothing = smoothing,
)

/**
 * Creates a [SquircleShape] with per-corner radius percentages defined as [Int] values.
 *
 * Each corner can have its own independent radius percentage.
 *
 * @param topStart The top start corner radius percentage from 0 to 100.
 * @param topEnd The top end corner radius percentage from 0 to 100.
 * @param bottomStart The bottom start corner radius percentage from 0 to 100.
 * @param bottomEnd The bottom end corner radius percentage from 0 to 100.
 * @param smoothing The smoothing factor from 0 to 100, defaults to [CornerSmoothing.Medium].
 * @return A new [SquircleShape] with the specified per-corner radii.
 *
 * @see SquircleShape
 * @see CornerSmoothing
 * @since 0.0.1
 **/
public fun SquircleShape(
    topStart: Int = 0,
    topEnd: Int = 0,
    bottomStart: Int = 0,
    bottomEnd: Int = 0,
    smoothing: Int = CornerSmoothing.Medium,
): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(topStart),
    topEndCorner = CornerSize(topEnd),
    bottomStartCorner = CornerSize(bottomStart),
    bottomEndCorner = CornerSize(bottomEnd),
    smoothing = smoothing,
)

/**
 * Creates a [SquircleShape] with per-corner radii defined as [Dp] values.
 *
 * Each corner can have its own independent radius.
 *
 * @param topStart The top start corner radius in density-independent pixels.
 * @param topEnd The top end corner radius in density-independent pixels.
 * @param bottomStart The bottom start corner radius in density-independent pixels.
 * @param bottomEnd The bottom end corner radius in density-independent pixels.
 * @param smoothing The smoothing factor from 0 to 100, defaults to [CornerSmoothing.Medium].
 * @return A new [SquircleShape] with the specified per-corner radii.
 *
 * @see SquircleShape
 * @see CornerSmoothing
 * @since 0.0.1
 **/
public fun SquircleShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    smoothing: Int = CornerSmoothing.Medium,
): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(topStart),
    topEndCorner = CornerSize(topEnd),
    bottomStartCorner = CornerSize(bottomStart),
    bottomEndCorner = CornerSize(bottomEnd),
    smoothing = smoothing,
)

/**
 * Creates a [SquircleShape] with per-corner radii in pixels defined as [Float] values.
 *
 * Each corner can have its own independent radius.
 *
 * @param topStart The top start corner radius in pixels.
 * @param topEnd The top end corner radius in pixels.
 * @param bottomStart The bottom start corner radius in pixels.
 * @param bottomEnd The bottom end corner radius in pixels.
 * @param smoothing The smoothing factor from 0 to 100, defaults to [CornerSmoothing.Medium].
 * @return A new [SquircleShape] with the specified per-corner radii.
 *
 * @see SquircleShape
 * @see CornerSmoothing
 * @since 0.0.1
 **/
public fun SquircleShape(
    topStart: Float = 0f,
    topEnd: Float = 0f,
    bottomStart: Float = 0f,
    bottomEnd: Float = 0f,
    smoothing: Int = CornerSmoothing.Medium,
): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(topStart),
    topEndCorner = CornerSize(topEnd),
    bottomStartCorner = CornerSize(bottomStart),
    bottomEndCorner = CornerSize(bottomEnd),
    smoothing = smoothing,
)

/**
 * Concrete implementation of [SquircleBasedShape] that produces a squircle outline
 * with configurable corner smoothing.
 *
 * This class delegates outline creation to [createSquircleShapeOutline], which in turn
 * uses [squircleShapePath] to build the underlying [Path] with cubic Bezier curves
 * controlled by the [smoothing] parameter.
 *
 * @param topStartCorner The top start corner radius defined as [CornerSize].
 * @param topEndCorner The top end corner radius defined as [CornerSize].
 * @param bottomStartCorner The bottom start corner radius defined as [CornerSize].
 * @param bottomEndCorner The bottom end corner radius defined as [CornerSize].
 * @param smoothing The corner smoothing from 0 to 100.
 *
 * @see SquircleBasedShape
 * @see GentleSquircleShape
 * @see CornerSmoothing
 * @see squircleShapePath
 * @since 0.0.1
 **/
public class SquircleShape(
    topStartCorner: CornerSize,
    topEndCorner: CornerSize,
    bottomStartCorner: CornerSize,
    bottomEndCorner: CornerSize,
    smoothing: Int,
) : SquircleBasedShape(
    topStart = topStartCorner,
    topEnd = topEndCorner,
    bottomStart = bottomStartCorner,
    bottomEnd = bottomEndCorner,
    smoothing = smoothing,
) {
    /**
     * Creates a copy of this shape with optionally different corner sizes,
     * preserving the current [smoothing] value.
     *
     * @param topStart The new top start corner size.
     * @param topEnd The new top end corner size.
     * @param bottomEnd The new bottom end corner size.
     * @param bottomStart The new bottom start corner size.
     * @return A new [SquircleShape] with the specified corner sizes and the same smoothing.
     *
     * @see SquircleBasedShape
     * @since 0.0.1
     */
    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
    ): SquircleShape = SquircleShape(
        topStartCorner = topStart,
        topEndCorner = topEnd,
        bottomStartCorner = bottomStart,
        bottomEndCorner = bottomEnd,
        smoothing = smoothing,
    )

    /**
     * Creates the [Outline] for this shape based on the resolved corner radii, smoothing,
     * and layout direction.
     *
     * @param size The size of the shape in pixels.
     * @param topStart The resolved top start corner radius in pixels.
     * @param topEnd The resolved top end corner radius in pixels.
     * @param bottomEnd The resolved bottom end corner radius in pixels.
     * @param bottomStart The resolved bottom start corner radius in pixels.
     * @param layoutDirection The current layout direction (LTR or RTL).
     * @return An [Outline] representing the squircle boundary path.
     *
     * @see createSquircleShapeOutline
     * @since 0.0.1
     */
    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection,
    ): Outline = createSquircleShapeOutline(
        size = size,
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        smoothing = smoothing,
        layoutDirection = layoutDirection,
    )

    /**
     * Returns a human-readable string representation of this shape including all corner
     * sizes and the smoothing value.
     *
     * @return A string describing this [SquircleShape] and its parameters.
     * @since 0.0.1
     */
    override fun toString(): String {
        return "SquircleShape(" +
            "topStart = $topStart, " +
            "topEnd = $topEnd, " +
            "bottomStart = $bottomStart, " +
            "bottomEnd = $bottomEnd, " +
            "smoothing = $smoothing" +
            ")"
    }

    /**
     * Checks structural equality by comparing all four corner sizes and the smoothing value.
     *
     * @param other The object to compare against.
     * @return `true` if [other] is a [SquircleShape] with identical corner sizes and smoothing,
     *   `false` otherwise.
     * @since 0.0.1
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SquircleShape) return false
        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomStart != other.bottomStart) return false
        if (bottomEnd != other.bottomEnd) return false
        if (smoothing != other.smoothing) return false
        return true
    }

    /**
     * Computes a hash code based on all four corner sizes and the smoothing value.
     *
     * @return The hash code value for this shape.
     * @since 0.0.1
     */
    override fun hashCode(): Int {
        var result = topStart.hashCode()
        // Multiply by 31 (a prime) to distribute hash codes and reduce collisions.
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + smoothing.hashCode()
        return result
    }
}
