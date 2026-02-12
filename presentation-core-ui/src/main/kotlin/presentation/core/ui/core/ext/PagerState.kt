package presentation.core.ui.core.ext

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Stable

@Stable
public fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    // Same semantics people used with Accompanist samples
    return (page - currentPage) + currentPageOffsetFraction
}