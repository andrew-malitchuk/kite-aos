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

/**
 *
 *  Base class for creating a Gentle Squircle shape derived from a [CornerBasedShape]
 *  defined by four corners.
 *
 *  @param topStart The top start corner radius defined as [CornerSize].
 *  @param topEnd The top end corner radius defined as [CornerSize].
 *  @param bottomStart The bottom start corner radius defined as [CornerSize].
 *  @param bottomEnd The bottom end corner radius defined as [CornerSize].
 *
 **/
public abstract class GentleSquircleBasedShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomStart: CornerSize,
    bottomEnd: CornerSize,
) : CornerBasedShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomStart = bottomStart,
    bottomEnd = bottomEnd,
)
