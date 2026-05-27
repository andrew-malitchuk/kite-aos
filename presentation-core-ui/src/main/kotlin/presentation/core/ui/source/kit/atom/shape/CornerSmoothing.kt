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

/**
 * A collection of commonly used corner smoothing [Int] values for a [SquircleShape].
 *
 * Corner smoothing controls how aggressively the superellipse curve is applied to each
 * corner. A value of 0 produces standard rounded corners (circular arcs), while 100
 * produces fully pronounced squircle corners with maximum curvature continuity.
 *
 * These preset values are intended to be passed as the `smoothing` parameter when
 * constructing a [SquircleShape] or [SquircleBasedShape].
 *
 * @see SquircleShape
 * @see SquircleBasedShape
 * @see GentleSquircleShape
 * @since 0.0.1
 */
public object CornerSmoothing {
    /**
     * Does not apply corner smoothing.
     * The result will be similar to a [RoundedCornerShape] with standard circular arc corners.
     *
     * @see SquircleShape
     * @since 0.0.1
     */
    // 0% smoothing -- corners are plain circular arcs with no superellipse blending.
    public val None: Int get() = 0

    /**
     * Applies a small amount of corner smoothing,
     * resulting in a slightly pronounced [SquircleShape].
     *
     * @see SquircleShape
     * @since 0.0.1
     */
    // 20% smoothing -- subtle squircle effect, close to rounded rectangles.
    public val Small: Int get() = 20

    /**
     * Applies a medium amount of corner smoothing,
     * resulting in a quite pronounced [SquircleShape].
     *
     * This is the default smoothing value used by [SquircleShape] factory functions.
     *
     * @see SquircleShape
     * @since 0.0.1
     */
    // 48% smoothing -- balanced midpoint between rounded and fully squircled corners.
    public val Medium: Int get() = 48

    /**
     * Applies a high amount of corner smoothing,
     * resulting in a highly pronounced [SquircleShape].
     *
     * @see SquircleShape
     * @since 0.0.1
     */
    // 67% smoothing -- strong superellipse curvature, close to iOS-style corners.
    public val High: Int get() = 67

    /**
     * Applies a full amount of corner smoothing,
     * resulting in a fully pronounced [SquircleShape].
     *
     * @see SquircleShape
     * @since 0.0.1
     */
    // 100% smoothing -- maximum superellipse effect for the most continuous curvature.
    public val Full: Int get() = 100
}
