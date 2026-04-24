/*
 * Copyright (c) 2025 Sylvain BARRÉ-PERSYN, Stoyan Vuchev
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

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import presentation.core.ui.source.kit.atom.shape.util.createGentleSquircleShapeOutline

/**
 * Creates a [GentleSquircleShape] that gently morphs from a squircle
 * into a circle when applying larger corner radii defined as an [Int] percentage value.
 *
 * All four corners share the same radius percentage.
 *
 * @param percent The corner radius percentage from 0 to 100, where 0 means no rounding
 *   and 100 means maximum rounding relative to the shape size.
 * @return A new [GentleSquircleShape] with uniform corner radii.
 *
 * @see GentleSquircleShape
 * @see GentleSquircleBasedShape
 * @see SquircleShape
 * @since 0.0.1
 **/
public fun GentleSquircleShape(percent: Int = 100): GentleSquircleShape = GentleSquircleShape(
    topStartCorner = CornerSize(percent),
    topEndCorner = CornerSize(percent),
    bottomStartCorner = CornerSize(percent),
    bottomEndCorner = CornerSize(percent),
)

/**
 * Creates a [GentleSquircleShape] that gently morphs from a squircle
 * into a circle when applying larger corner radii defined as a [Dp] value.
 *
 * All four corners share the same radius.
 *
 * @param radius The corner radius in density-independent pixels.
 * @return A new [GentleSquircleShape] with uniform corner radii.
 *
 * @see GentleSquircleShape
 * @see GentleSquircleBasedShape
 * @since 0.0.1
 **/
public fun GentleSquircleShape(radius: Dp): GentleSquircleShape = GentleSquircleShape(
    topStartCorner = CornerSize(radius),
    topEndCorner = CornerSize(radius),
    bottomStartCorner = CornerSize(radius),
    bottomEndCorner = CornerSize(radius),
)

/**
 * Creates a [GentleSquircleShape] that gently morphs from a squircle
 * into a circle when applying larger corner radii defined as a [Float] value.
 *
 * All four corners share the same radius.
 *
 * @param radius The corner radius in pixels.
 * @return A new [GentleSquircleShape] with uniform corner radii.
 *
 * @see GentleSquircleShape
 * @see GentleSquircleBasedShape
 * @since 0.0.1
 **/
public fun GentleSquircleShape(radius: Float): GentleSquircleShape = GentleSquircleShape(
    topStartCorner = CornerSize(radius),
    topEndCorner = CornerSize(radius),
    bottomStartCorner = CornerSize(radius),
    bottomEndCorner = CornerSize(radius),
)

/**
 * Creates a [GentleSquircleShape] that gently morphs from a squircle
 * into a circle when applying larger corner radii defined as [Int] percentage values.
 *
 * Each corner can have its own independent radius percentage.
 *
 * @param topStart The top start corner radius percentage from 0 to 100.
 * @param topEnd The top end corner radius percentage from 0 to 100.
 * @param bottomStart The bottom start corner radius percentage from 0 to 100.
 * @param bottomEnd The bottom end corner radius percentage from 0 to 100.
 * @return A new [GentleSquircleShape] with the specified per-corner radii.
 *
 * @see GentleSquircleShape
 * @see GentleSquircleBasedShape
 * @since 0.0.1
 **/
public fun GentleSquircleShape(
    topStart: Int = 0,
    topEnd: Int = 0,
    bottomStart: Int = 0,
    bottomEnd: Int = 0,
): GentleSquircleShape = GentleSquircleShape(
    topStartCorner = CornerSize(topStart),
    topEndCorner = CornerSize(topEnd),
    bottomStartCorner = CornerSize(bottomStart),
    bottomEndCorner = CornerSize(bottomEnd),
)

/**
 * Creates a [GentleSquircleShape] that gently morphs from a squircle
 * into a circle when applying larger corner radii defined as [Dp] values.
 *
 * Each corner can have its own independent radius.
 *
 * @param topStart The top start corner radius in density-independent pixels.
 * @param topEnd The top end corner radius in density-independent pixels.
 * @param bottomStart The bottom start corner radius in density-independent pixels.
 * @param bottomEnd The bottom end corner radius in density-independent pixels.
 * @return A new [GentleSquircleShape] with the specified per-corner radii.
 *
 * @see GentleSquircleShape
 * @see GentleSquircleBasedShape
 * @since 0.0.1
 **/
public fun GentleSquircleShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
): GentleSquircleShape = GentleSquircleShape(
    topStartCorner = CornerSize(topStart),
    topEndCorner = CornerSize(topEnd),
    bottomStartCorner = CornerSize(bottomStart),
    bottomEndCorner = CornerSize(bottomEnd),
)

/**
 * Creates a [GentleSquircleShape] that gently morphs from a squircle
 * into a circle when applying larger corner radii defined as [Float] values.
 *
 * Each corner can have its own independent radius in pixels.
 *
 * @param topStart The top start corner radius in pixels.
 * @param topEnd The top end corner radius in pixels.
 * @param bottomStart The bottom start corner radius in pixels.
 * @param bottomEnd The bottom end corner radius in pixels.
 * @return A new [GentleSquircleShape] with the specified per-corner radii.
 *
 * @see GentleSquircleShape
 * @see GentleSquircleBasedShape
 * @since 0.0.1
 **/
public fun GentleSquircleShape(
    topStart: Float = 0f,
    topEnd: Float = 0f,
    bottomStart: Float = 0f,
    bottomEnd: Float = 0f,
): GentleSquircleShape = GentleSquircleShape(
    topStartCorner = CornerSize(topStart),
    topEndCorner = CornerSize(topEnd),
    bottomStartCorner = CornerSize(bottomStart),
    bottomEndCorner = CornerSize(bottomEnd),
)

/**
 * Concrete implementation of [GentleSquircleBasedShape] that produces a squircle outline
 * which gently transitions to a circular arc when corner radii become large relative to the
 * shape dimensions.
 *
 * This class delegates outline creation to [createGentleSquircleShapeOutline], which in turn
 * uses [gentleSquircleShapePath] to build the underlying [Path].
 *
 * @param topStartCorner The top start corner radius defined as [CornerSize].
 * @param topEndCorner The top end corner radius defined as [CornerSize].
 * @param bottomStartCorner The bottom start corner radius defined as [CornerSize].
 * @param bottomEndCorner The bottom end corner radius defined as [CornerSize].
 *
 * @see GentleSquircleBasedShape
 * @see SquircleShape
 * @see gentleSquircleShapePath
 * @since 0.0.1
 **/
public class GentleSquircleShape(
    topStartCorner: CornerSize,
    topEndCorner: CornerSize,
    bottomStartCorner: CornerSize,
    bottomEndCorner: CornerSize,
) : GentleSquircleBasedShape(
    topStart = topStartCorner,
    topEnd = topEndCorner,
    bottomStart = bottomStartCorner,
    bottomEnd = bottomEndCorner,
) {
    /**
     * Creates a copy of this shape with optionally different corner sizes.
     *
     * @param topStart The new top start corner size.
     * @param topEnd The new top end corner size.
     * @param bottomEnd The new bottom end corner size.
     * @param bottomStart The new bottom start corner size.
     * @return A new [GentleSquircleShape] with the specified corner sizes.
     *
     * @see GentleSquircleBasedShape
     * @since 0.0.1
     */
    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
    ): CornerBasedShape = GentleSquircleShape(
        topStartCorner = topStart,
        topEndCorner = topEnd,
        bottomStartCorner = bottomStart,
        bottomEndCorner = bottomEnd,
    )

    /**
     * Creates the [Outline] for this shape based on the resolved corner radii and layout direction.
     *
     * @param size The size of the shape in pixels.
     * @param topStart The resolved top start corner radius in pixels.
     * @param topEnd The resolved top end corner radius in pixels.
     * @param bottomEnd The resolved bottom end corner radius in pixels.
     * @param bottomStart The resolved bottom start corner radius in pixels.
     * @param layoutDirection The current layout direction (LTR or RTL).
     * @return An [Outline] representing the gentle squircle boundary path.
     *
     * @see createGentleSquircleShapeOutline
     * @since 0.0.1
     */
    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection,
    ): Outline = createGentleSquircleShapeOutline(
        size = size,
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        layoutDirection = layoutDirection,
    )

    /**
     * Returns a human-readable string representation of this shape including all corner sizes.
     *
     * @return A string describing this [GentleSquircleShape] and its corner radii.
     * @since 0.0.1
     */
    override fun toString(): String {
        return "GentleSquircleShape(" +
            "topStart = $topStart, " +
            "topEnd = $topEnd, " +
            "bottomStart = $bottomStart, " +
            "bottomEnd = $bottomEnd" +
            ")"
    }

    /**
     * Checks structural equality by comparing all four corner sizes.
     *
     * @param other The object to compare against.
     * @return `true` if [other] is a [SquircleShape] with identical corner sizes, `false` otherwise.
     * @since 0.0.1
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SquircleShape) return false
        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomStart != other.bottomStart) return false
        if (bottomEnd != other.bottomEnd) return false
        return true
    }

    /**
     * Computes a hash code based on all four corner sizes.
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
        return result
    }
}
