package presentation.core.ui.core.ext

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Stable

/**
 * Calculates the current scroll offset for the given [page] relative to the current page position.
 *
 * This provides the same semantics as the offset calculation from the legacy Accompanist Pager
 * samples: a value of 0 means the page is fully visible, negative values indicate the page is
 * to the left, and positive values indicate it is to the right.
 *
 * @param page the page index to calculate the offset for.
 * @return the floating-point offset of [page] relative to the current page and scroll fraction.
 *
 * @since 0.0.1
 */
@Stable
public fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    // Same semantics people used with Accompanist samples
    return (page - currentPage) + currentPageOffsetFraction
}
