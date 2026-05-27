/*
 * Copyright (c) 2023-2025 Stoyan Vuchev, Sylvain BARRÉ-PERSYN
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

package presentation.core.ui.source.kit.atom.shape.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.LayoutDirection
import presentation.core.ui.source.kit.atom.shape.GentleSquircleShape
import presentation.core.ui.source.kit.atom.shape.SquircleShape
import presentation.core.ui.source.kit.atom.shape.gentleSquircleShapePath
import presentation.core.ui.source.kit.atom.shape.squircleShapePath

/**
 * Creates an [Outline] for a [SquircleShape], considering corner radii, smoothing, and
 * layout direction.
 *
 * This function serves as the bridge between the Compose shape system and the squircle
 * path builder. It handles RTL mirroring of corners and delegates to [squircleShapePath]
 * for the actual path construction.
 *
 * @param size The overall size of the shape as a [Size] object.
 * @param topStart The radius of the top-start corner in pixels. In LTR layouts, this
 *   corresponds to the top-left corner.
 * @param topEnd The radius of the top-end corner in pixels. In LTR layouts, this
 *   corresponds to the top-right corner.
 * @param bottomEnd The radius of the bottom-end corner in pixels. In LTR layouts, this
 *   corresponds to the bottom-right corner.
 * @param bottomStart The radius of the bottom-start corner in pixels. In LTR layouts, this
 *   corresponds to the bottom-left corner.
 * @param smoothing An integer value between 0 and 100 that determines the corner smoothing.
 * @param layoutDirection The layout direction ([LayoutDirection.Ltr] or [LayoutDirection.Rtl])
 *   that controls how corner radii are mapped to physical corners.
 * @return An [Outline.Generic] containing the squircle path.
 *
 * @see SquircleShape
 * @see squircleShapePath
 * @see createGentleSquircleShapeOutline
 * @see clampedSmoothing
 * @since 0.0.1
 */
@Stable
internal fun createSquircleShapeOutline(
    size: Size,
    topStart: Float,
    topEnd: Float,
    bottomEnd: Float,
    bottomStart: Float,
    smoothing: Int,
    layoutDirection: LayoutDirection,
): Outline {
    // Mirror start/end corners for RTL layout direction.
    val isRtl = layoutDirection == LayoutDirection.Rtl
    return Outline.Generic(
        path =
        squircleShapePath(
            size = size,
            topLeftCorner = if (isRtl) topEnd else topStart,
            topRightCorner = if (isRtl) topStart else topEnd,
            bottomLeftCorner = if (isRtl) bottomEnd else bottomStart,
            bottomRightCorner = if (isRtl) bottomStart else bottomEnd,
            smoothing = clampedSmoothing(smoothing),
        ),
    )
}

/**
 * Creates an [Outline] for a [GentleSquircleShape], considering corner radii and layout direction.
 *
 * This function serves as the bridge between the Compose shape system and the gentle squircle
 * path builder. It handles RTL mirroring of corners and delegates to [gentleSquircleShapePath]
 * for the actual path construction.
 *
 * @param size The overall size of the shape as a [Size] object.
 * @param topStart The radius of the top-start corner in pixels. In LTR layouts, this
 *   corresponds to the top-left corner.
 * @param topEnd The radius of the top-end corner in pixels. In LTR layouts, this
 *   corresponds to the top-right corner.
 * @param bottomEnd The radius of the bottom-end corner in pixels. In LTR layouts, this
 *   corresponds to the bottom-right corner.
 * @param bottomStart The radius of the bottom-start corner in pixels. In LTR layouts, this
 *   corresponds to the bottom-left corner.
 * @param layoutDirection The layout direction ([LayoutDirection.Ltr] or [LayoutDirection.Rtl])
 *   that controls how corner radii are mapped to physical corners.
 * @return An [Outline.Generic] containing the gentle squircle path.
 *
 * @see GentleSquircleShape
 * @see gentleSquircleShapePath
 * @see createSquircleShapeOutline
 * @since 0.0.1
 */
@Stable
internal fun createGentleSquircleShapeOutline(
    size: Size,
    topStart: Float,
    topEnd: Float,
    bottomEnd: Float,
    bottomStart: Float,
    layoutDirection: LayoutDirection,
): Outline {
    // Mirror start/end corners for RTL layout direction.
    val isRtl = layoutDirection == LayoutDirection.Rtl
    return Outline.Generic(
        path =
        gentleSquircleShapePath(
            size = size,
            topLeftCorner = if (isRtl) topEnd else topStart,
            topRightCorner = if (isRtl) topStart else topEnd,
            bottomLeftCorner = if (isRtl) bottomEnd else bottomStart,
            bottomRightCorner = if (isRtl) bottomStart else bottomEnd,
        ),
    )
}
