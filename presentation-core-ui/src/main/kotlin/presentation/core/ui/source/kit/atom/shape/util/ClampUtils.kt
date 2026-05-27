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

package presentation.core.ui.source.kit.atom.shape.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size

/**
 * Clamps the corner radius from 0.0f to half of the shape's smallest dimension.
 *
 * This simple variant is used by [gentleSquircleShapePath] where corner smoothing
 * is handled separately via [clampedSmoothing].
 *
 * @param cornerSize The corner radius in pixels.
 * @param size The size of the shape.
 * @return The clamped corner radius, guaranteed to be in [0, minDimension / 2].
 *
 * @see clampedCornerRadius
 * @see gentleSquircleShapePath
 * @since 0.0.1
 **/
@Stable
internal fun clampedCornerRadius(cornerSize: Float, size: Size): Float {
    // Half the smallest axis is the maximum radius before corners would overlap.
    val smallestAxis = size.minDimension / 2
    return cornerSize.coerceIn(0f, smallestAxis)
}

/**
 * Clamps the corner radius from 0.0f to half of the shape's smallest dimension,
 * applying a smoothing-dependent expansion factor.
 *
 * This variant is used by [squircleShapePath] where the smoothing parameter affects
 * how much the corner radius is expanded before clamping. The expansion compensates
 * for the visual shrinkage caused by the cubic Bezier control point placement at
 * higher smoothing values.
 *
 * The algorithm:
 * 1. Normalizes [smoothing] from 0-100 to 0.0-1.0 (`smoothness`).
 * 2. Computes a triangular amplitude via [triangleFraction] that peaks at smoothing=50%,
 *    clamped to a minimum of 0.75 to prevent excessive expansion at extremes.
 * 3. Maps the attenuated smoothness through [transformFraction] to an expansion factor
 *    in the range [1.0, 2.33], where 2.33 is the maximum expansion at full smoothing.
 * 4. Multiplies the corner radius by this factor and clamps to the smallest axis.
 *
 * @param size The size of the shape.
 * @param cornerSize The corner radius in pixels.
 * @param smoothing The corner smoothing of the shape from 0 to 100.
 * @return The expanded and clamped corner radius.
 *
 * @see clampedCornerRadius
 * @see squircleShapePath
 * @see triangleFraction
 * @see transformFraction
 * @since 0.0.1
 **/
@Stable
internal fun clampedCornerRadius(size: Size, cornerSize: Float, smoothing: Int): Float {
    // Half the smallest axis is the maximum radius before corners would overlap.
    val smallestAxis = size.minDimension / 2
    // Normalize the integer smoothing (0-100) to a unit float (0.0-1.0).
    val smoothness = smoothing.toFloat() / 100
    // The triangular wave peaks at 0.5, so the amplitude is lowest at the extremes (0, 1)
    // and highest at the midpoint. Clamping to 0.75 prevents over-expansion at mid-range.
    val smoothnessAmplitude = (1f - triangleFraction(smoothness)).coerceAtLeast(.75f)
    // Map the attenuated smoothness to an expansion factor in [1.0, 2.33].
    // The magic number 2.33 was empirically chosen to keep the visual corner size
    // consistent across different smoothing values.
    val factor =
        transformFraction(
            value = smoothness * smoothnessAmplitude,
            startX = 0f,
            endX = 1f,
            startY = 1f,
            endY = 2.33f,
        )

    return (cornerSize * factor).coerceIn(0f, smallestAxis)
}

/**
 * Clamps the corner smoothing integer value to the valid range of 0 to 100.
 *
 * @param smoothing The corner smoothing value to clamp.
 * @return The smoothing value clamped to [0, 100].
 *
 * @see CornerSmoothing
 * @see clampedSmoothing
 * @since 0.0.1
 **/
@Suppress("MagicNumber")
@Stable
internal fun clampedSmoothing(smoothing: Int): Int = smoothing.coerceIn(0, 100)

/**
 * Computes a smoothing factor that gradually decreases as the corner radius approaches
 * the corner threshold, used by the gentle squircle algorithm.
 *
 * The smoothing factor is linearly interpolated from 0.0 (at radius=0) to 0.45
 * (at radius=cornerThreshold). The magic number 0.45 controls the maximum offset
 * of the cubic Bezier control points relative to the corner radius, producing
 * a visually pleasing squircle curvature at maximum radius before the arc fallback.
 *
 * @param radius The current corner radius in pixels.
 * @param cornerThreshold The radius threshold (half the smallest dimension) at which
 *   the gentle squircle switches from Bezier curves to circular arcs.
 * @return A smoothing factor in [0.0, 0.45] controlling the Bezier control point offset.
 *
 * @see gentleSquircleShapePath
 * @see transformFraction
 * @since 0.0.1
 **/
@Stable
internal fun clampedSmoothing(radius: Float, cornerThreshold: Float): Float = transformFraction(
    value = radius.coerceAtMost(cornerThreshold),
    startX = 0f,
    endX = cornerThreshold,
    startY = 0f,
    // 0.45 is the empirically chosen maximum Bezier offset ratio for the gentle squircle.
    endY = .45f,
)
