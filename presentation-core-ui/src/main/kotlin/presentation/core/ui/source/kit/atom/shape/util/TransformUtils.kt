/*
 * Copyright (c) 2025 Stoyan Vuchev
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

/**
 * Linearly transforms (maps) a [Float] value from one range to another.
 *
 * This is a standard linear interpolation/extrapolation utility used throughout the
 * squircle shape system to convert between different numeric domains (e.g., smoothing
 * percentages to Bezier offset factors).
 *
 * The transformation formula is:
 * ```
 * result = ((value - startX) / (endX - startX)) * (endY - startY) + startY
 * ```
 *
 * Input and output ranges are automatically normalized so that start is always less
 * than or equal to end, regardless of the order in which they are provided.
 *
 * @param value The input value to be transformed.
 * @param startX The start of the input range (defaults to 0.0f).
 * @param endX The end of the input range (defaults to 1.0f).
 * @param startY The start of the output range.
 * @param endY The end of the output range.
 * @return The transformed value mapped from [startX, endX] to [startY, endY].
 *
 * @see convertIntBasedSmoothingToFloat
 * @see clampedCornerRadius
 * @see clampedSmoothing
 * @since 0.0.1
 */
internal fun transformFraction(value: Float, startX: Float = 0f, endX: Float = 1f, startY: Float, endY: Float): Float {
    // Normalize input range so that newStartX <= newEndX, ensuring a positive denominator.
    val newStartX = if (startX <= endX) startX else endX
    val newEndX = if (startX <= endX) endX else startX

    // Normalize output range so that newStartY <= newEndY.
    val newStartY = if (startY <= endY) startY else endY
    val newEndY = if (startY <= endY) endY else startY

    // Apply the standard linear interpolation formula:
    // normalized = (value - inputMin) / (inputMax - inputMin)
    // result     = normalized * (outputMax - outputMin) + outputMin
    return ((value - newStartX) / (newEndX - newStartX)) * (newEndY - newStartY) + newStartY
}

/**
 * Converts an integer-based corner smoothing value (0-100) to a float-based smoothing
 * factor in the range [0.55, 1.0].
 *
 * The output range of 0.55 to 1.0 was empirically chosen:
 * - 0.55 (at smoothing=0) means the Bezier control points are offset by 45% of the
 *   corner radius from the edge, producing standard rounded corners.
 * - 1.0 (at smoothing=100) means the control points sit exactly at the corner,
 *   producing the most pronounced squircle effect.
 *
 * @param smoothing The corner smoothing value from 0 to 100.
 * @return The transformed smoothing factor in [0.55, 1.0].
 *
 * @see transformFraction
 * @see squircleShapePath
 * @see CornerSmoothing
 * @since 0.0.1
 */
internal fun convertIntBasedSmoothingToFloat(smoothing: Int): Float = transformFraction(
    value = smoothing.toFloat(),
    startX = 0f,
    endX = 100f,
    // 0.55 is the minimum smoothing factor, producing standard rounded-corner behavior.
    startY = .55f,
    // 1.0 is the maximum, producing fully pronounced squircle corners.
    endY = 1f,
)

/**
 * Computes a triangular wave function for a given fraction value.
 *
 * This function linearly interpolates between `1f` and `0f` based on the input fraction:
 * - Returns `1f` when `value = 0f` (no smoothing)
 * - Decreases linearly to `0f` when `value = 0.5f` (midpoint)
 * - Increases back to `1f` when `value = 1f` (full smoothing)
 *
 * Mathematically, it follows the equation:
 * ```
 * f(x) = 1 - 2 * |x - 0.5|
 * ```
 *
 * This function is used in [clampedCornerRadius] to compute the smoothness amplitude,
 * which attenuates the corner expansion factor at extreme smoothing values (0 and 100)
 * and maximizes it at the midpoint (50).
 *
 * @param value A floating-point value in the range `[0f, 1f]`, representing the normalized
 *   smoothing fraction.
 * @return A floating-point value in the range `[0f, 1f]`, following a triangular wave pattern.
 *
 * @see clampedCornerRadius
 * @since 0.0.1
 */
@Suppress("MagicNumber")
internal fun triangleFraction(value: Float): Float {
    // The magic numbers 2f and 0.5f define the triangular wave:
    // - 0.5f is the midpoint where the function reaches its minimum (0.0).
    // - 2f scales the absolute deviation to span the full [0, 1] output range.
    return 1f - 2f * kotlin.math.abs(value - 0.5f)
}
