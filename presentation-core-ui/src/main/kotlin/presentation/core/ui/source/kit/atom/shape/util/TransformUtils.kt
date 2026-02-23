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
 * A utility function to transform a [Float] fraction value from one range to another.
 *
 * @param value The input value to be transformed.
 * @param startX The start of the input range.
 * @param endX The end of the input range.
 * @param startY The start of the output range.
 * @param endY The end of the output range.
 *
 * @return The transformed value.
 */
internal fun transformFraction(
    value: Float,
    startX: Float = 0f,
    endX: Float = 1f,
    startY: Float,
    endY: Float
): Float {

    // Check if startX is less than endX
    val newStartX = if (startX <= endX) startX else endX
    val newEndX = if (startX <= endX) endX else startX

    // Check if startY is less than endY
    val newStartY = if (startY <= endY) startY else endY
    val newEndY = if (startY <= endY) endY else startY

    // Transform the value to the new range
    return ((value - newStartX) / (newEndX - newStartX)) * (newEndY - newStartY) + newStartY

}

/**
 * A helper function to transform an [Int] based
 * corner smoothing value to a [Float] value.
 *
 * @param smoothing The corner smoothing value.
 *
 * @return The transformed smoothing value.
 */
internal fun convertIntBasedSmoothingToFloat(
    smoothing: Int
): Float = transformFraction(
    value = smoothing.toFloat(),
    startX = 0f,
    endX = 100f,
    startY = .55f,
    endY = 1f
)

/**
 * Computes a triangular wave function for a given fraction value.
 *
 * This function smoothly interpolates the value between `1f` and `0f` based on the input fraction:
 * - Starts at `1f` when `fraction = 0f`
 * - Decreases linearly to `0f` when `fraction = 0.5f`
 * - Increases back to `1f` when `fraction = 1f`
 *
 * Mathematically, it follows the equation:
 * ```
 * f(x) = 1 - 2 * |x - 0.5|
 * ```
 *
 * @param fraction A floating-point value in the range `[0f, 1f]`, representing the input fraction.
 * @return A floating-point value in the range `[0f, 1f]`, following a triangular wave pattern.
 */
internal fun triangleFraction(value: Float): Float {
    return 1f - 2f * kotlin.math.abs(value - 0.5f)
}