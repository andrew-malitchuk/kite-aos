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
                        cameraDistance = 8 * density
                    },
                contentAlignment = Alignment.Center,
            ) {
                itemContent(item)
            }
        }
    }
}

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
                        cameraDistance = 8 * density
                    },
                contentAlignment = Alignment.Center,
            ) {
                itemContent(page)
            }
        }
    }
}
