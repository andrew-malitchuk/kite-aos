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
 *
 *  Creates a [SquircleShape] with corner radius percent defined as [Int] value.
 *
 *  @param percent The corner radius percent from 0 to 100.
 *  @param smoothing The smoothing factor from 0 to 100.
 *
 **/
public fun SquircleShape(percent: Int = 100, smoothing: Int = CornerSmoothing.Medium): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(percent),
    topEndCorner = CornerSize(percent),
    bottomStartCorner = CornerSize(percent),
    bottomEndCorner = CornerSize(percent),
    smoothing = smoothing,
)

/**
 *
 *  Creates a [SquircleShape] with corner radius defined as [Dp] value.
 *
 *  @param radius The corner radius.
 *  @param smoothing The smoothing factor from 0 to 100.
 *
 **/
public fun SquircleShape(radius: Dp, smoothing: Int = CornerSmoothing.Medium): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(radius),
    topEndCorner = CornerSize(radius),
    bottomStartCorner = CornerSize(radius),
    bottomEndCorner = CornerSize(radius),
    smoothing = smoothing,
)

/**
 *
 *  Creates a [SquircleShape] with corner radius in pixels defined as [Float] value.
 *
 *  @param radius The corner radius in pixels.
 *  @param smoothing The smoothing factor from 0 to 100.
 *
 **/
public fun SquircleShape(radius: Float, smoothing: Int = CornerSmoothing.Medium): SquircleShape = SquircleShape(
    topStartCorner = CornerSize(radius),
    topEndCorner = CornerSize(radius),
    bottomStartCorner = CornerSize(radius),
    bottomEndCorner = CornerSize(radius),
    smoothing = smoothing,
)

/**
 *
 *  Creates a [SquircleShape] with corners percent defined as [Int] values.
 *
 *  @param topStart The top start corner radius percent from 0 to 100.
 *  @param topEnd The top end corner radius percent from 0 to 100.
 *  @param bottomStart The bottom start corner radius percent from 0 to 100.
 *  @param bottomEnd The bottom end corner radius percent from 0 to 100.
 *  @param smoothing The smoothing factor from 0 to 100.
 *
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
 *
 *  Creates a [SquircleShape] with corners defined as [Dp] values.
 *
 *  @param topStart The top start corner radius.
 *  @param topEnd The top end corner radius.
 *  @param bottomStart The bottom start corner radius.
 *  @param bottomEnd The bottom end corner radius.
 *  @param smoothing The smoothing factor from 0 to 100.
 *
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
 *
 *  Creates a [SquircleShape] with pixel corners defined as [Float] values.
 *
 *  @param topStart The top start corner radius in pixels.
 *  @param topEnd The top end corner radius.
 *  @param bottomStart The bottom start corner radius.
 *  @param bottomEnd The bottom end corner radius.
 *  @param smoothing The smoothing factor from 0 to 100.
 *
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
 *
 *  Creates a [SquircleBasedShape].
 *
 *  @param topStartCorner The top start corner radius defined as [CornerSize].
 *  @param topEndCorner The top end corner radius defined as [CornerSize].
 *  @param bottomStartCorner The bottom start corner radius defined as [CornerSize].
 *  @param bottomEndCorner The bottom end corner radius defined as [CornerSize].
 *  @param smoothing The corner smoothing from 0 to 100.
 *
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

    override fun toString(): String {
        return "SquircleShape(" +
            "topStart = $topStart, " +
            "topEnd = $topEnd, " +
            "bottomStart = $bottomStart, " +
            "bottomEnd = $bottomEnd, " +
            "smoothing = $smoothing" +
            ")"
    }

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

    override fun hashCode(): Int {
        var result = topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + smoothing.hashCode()
        return result
    }
}
