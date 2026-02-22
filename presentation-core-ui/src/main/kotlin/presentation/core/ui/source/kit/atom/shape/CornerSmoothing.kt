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

import presentation.core.ui.source.kit.atom.shape.SquircleShape

/**
 * A collection of commonly used corner smoothing [Int] values for a [SquircleShape].
 */
public object CornerSmoothing {

    /**
     * Does not apply corner smoothing.
     * The result will be similar to a [RoundedCornerShape].
     */
    public val None: Int get() = 0

    /**
     * Applies a small amount of corner smoothing,
     * resulting slightly pronounced [SquircleShape].
     */
    public val Small: Int get() = 20

    /**
     * Applies a medium amount of corner smoothing,
     * resulting quite pronounced [SquircleShape].
     */
    public val Medium: Int get() = 48

    /**
     * Applies a high amount of corner smoothing,
     * resulting highly pronounced [SquircleShape].
     */
    public val High: Int get() = 67

    /**
     * Applies a full amount of corner smoothing,
     * resulting fully pronounced [SquircleShape].
     */
    public val Full: Int get() = 100

}