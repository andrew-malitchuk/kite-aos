package dev.yamh.io.presentation.core.ui.source.kit.atom.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue

/**
 * A vertical pager that compresses (scales down) off-screen pages to create a depth effect.
 *
 * Pages scale down by up to 15% based on their distance from the current page, giving the
 * impression of a card stack. This overload accepts a typed list of items.
 *
 * @param T the type of items in the pager.
 * @param modifier Modifier to be applied to the [VerticalPager].
 * @param items the list of items to display, one per page.
 * @param pagerState the [PagerState] controlling the pager's current page and scroll position.
 * @param itemContent composable content for each item.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun <T> VerticalCompressingPager(
    modifier: Modifier = Modifier,
    items: List<T>,
    pagerState: PagerState,
    itemContent: @Composable (T) -> Unit,
) {
    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
    ) { page ->
        val item = items[page]
        val offset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val absOffset = offset.absoluteValue

        // Scale down by up to 15% based on distance from the current page
        val scale = 1f - (0.15f * absOffset).coerceAtMost(0.15f)

        Box(
            modifier =
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        // Multiplied by density for device-independent perspective distance
                        cameraDistance = 8 * density
                    },
                contentAlignment = Alignment.Center,
            ) {
                itemContent(item)
            }
        }
    }
}

/**
 * A vertical pager that compresses (scales down) off-screen pages to create a depth effect.
 *
 * Pages scale down by up to 15% based on their distance from the current page, giving the
 * impression of a card stack. This overload accepts a page index instead of typed items.
 *
 * @param modifier Modifier to be applied to the [VerticalPager].
 * @param pagerState the [PagerState] controlling the pager's current page and scroll position.
 * @param itemContent composable content for each page, receiving the page index.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun VerticalCompressingPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    itemContent: @Composable (Int) -> Unit,
) {
    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
    ) { page ->
        val offset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val absOffset = offset.absoluteValue

        // Scale down by up to 15% based on distance from the current page
        val scale = 1f - (0.15f * absOffset).coerceAtMost(0.15f)

        Box(
            modifier =
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        // Multiplied by density for device-independent perspective distance
                        cameraDistance = 8 * density
                    },
                contentAlignment = Alignment.Center,
            ) {
                itemContent(page)
            }
        }
    }
}
