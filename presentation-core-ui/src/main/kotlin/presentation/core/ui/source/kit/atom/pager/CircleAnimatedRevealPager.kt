package presentation.core.ui.source.kit.atom.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun CircleAnimatedRevealPager(
    count: Int,
    modifier: Modifier = Modifier,
    clipCornerRadius: Dp = 25.dp,
    state: PagerState = rememberPagerState(initialPage = 0, pageCount = { count }),
    // If you want a fully generic pager, provide your own content
    pageContent: @Composable (page: Int) -> Unit,
) {
    var offsetY by remember { mutableStateOf(0f) }

    HorizontalPager(
        state = state,
        modifier =
        modifier
            .clip(RoundedCornerShape(clipCornerRadius))
            .pointerInput(Unit) {
                // Cross-platform pointer tracking
                awaitPointerEventScope {
                    while (true) {
                        val e = awaitPointerEvent()
                        val pos = e.changes.firstOrNull()?.position
                        if (pos != null) offsetY = pos.y
                    }
                }
            },
    ) { page ->
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val pageOffset = state.offsetForPage(page)
                    translationX = size.width * pageOffset

                    val endOffset = state.endOffsetForPage(page)
                    shape =
                        CirclePath(
                            progress = 1f - endOffset.absoluteValue,
                            origin = Offset(size.width, offsetY),
                        )
                    clip = true

                    val absoluteOffset = pageOffset.absoluteValue
                    val scale = 1f + (absoluteOffset * .4f)
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center,
        ) {
            pageContent(page)
        }
    }
}

// ===== PagerState helpers: KMP-safe =====
@OptIn(ExperimentalFoundationApi::class)
private fun PagerState.offsetForPage(page: Int): Float {
    // Current page to target page offset: negative when target is to the right
    return (currentPage - page) + currentPageOffsetFraction
}

@OptIn(ExperimentalFoundationApi::class)
private fun PagerState.startOffsetForPage(page: Int): Float {
    // Positive when the page is ahead of the current scroll
    return (page - currentPage) - currentPageOffsetFraction
}

@OptIn(ExperimentalFoundationApi::class)
private fun PagerState.endOffsetForPage(page: Int): Float {
    // Positive when the page is behind the current scroll
    return (currentPage - page) + currentPageOffsetFraction
}

// ===== Circle clip shape used for the reveal =====
private class CirclePath(
    private val progress: Float,
    private val origin: Offset = Offset(0f, 0f),
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val center =
            Offset(
                x = size.center.x - ((size.center.x - origin.x) * (1f - progress)),
                y = size.center.y - ((size.center.y - origin.y) * (1f - progress)),
            )
        val radius = (sqrt(size.height * size.height + size.width * size.width) * .5f) * progress

        return Outline.Generic(
            Path().apply {
                addOval(Rect(center = center, radius = radius))
            },
        )
    }
}
