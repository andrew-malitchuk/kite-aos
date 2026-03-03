package presentation.feature.onboarding.core.composable.pager

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import presentation.core.localisation.R
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.primary.PrimaryButton
import presentation.core.ui.source.kit.atom.button.primary.core.PrimaryButtonDefault

/**
 * Represents the possible actions triggered by the [WizardPager].
 */
public sealed interface WizardPagerAction {
    /** Triggered when the final page's finish button is clicked. */
    public data object OnFinishClick : WizardPagerAction
    /** Triggered when the next button is clicked to move to the next slide. */
    public data object OnNextClick : WizardPagerAction
    /** Triggered when the back button is clicked to move to the previous slide. */
    public data object OnBackClick : WizardPagerAction
}

/**
 * Data model for a single page in the [WizardPager].
 *
 * @param title The title displayed at the bottom of the page.
 * @param description A brief description displayed under the title.
 * @param backgroundColor The target background color for this specific page.
 * @param isNextEnabled A lambda returning whether the "Next" button should be interactable.
 * @param content The Composable content to display in the main pager area.
 */
public data class WizardPageData(
    val title: String,
    val description: String,
    val backgroundColor: Color,
    val isNextEnabled: () -> Boolean = { true },
    val content: @Composable () -> Unit,
)

/**
 * A multi-step onboarding wizard component.
 *
 * It uses an internal [HorizontalPager] to transition between [pages]. The background color
 * animates smoothly as the user navigates. Navigation is controlled via "Previous" and
 * "Next/Finish" buttons at the bottom.
 *
 * @param modifier The modifier to be applied to the wizard layout.
 * @param pages The list of [WizardPageData] representing the wizard steps.
 * @param onAction Callback for handling navigation actions.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun WizardPager(
    modifier: Modifier = Modifier,
    pages: List<WizardPageData>,
    onAction: (WizardPagerAction) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val currentPageData = pages[pagerState.currentPage]
    val animatedBackgroundColor by animateColorAsState(
        targetValue = currentPageData.backgroundColor,
        label = "background color"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBackgroundColor)
            .padding(Theme.spacing.sizeM)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false,
        ) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).coerceIn(-1f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - kotlin.math.abs(pageOffset)

                        translationX = pageOffset * size.width
                    }
            ) {
                pages[page].content()
            }
        }

        // Page Indicator
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            repeat(pages.size) { iteration ->
                val indicatorColor by animateColorAsState(
                    targetValue = if (pagerState.currentPage == iteration) Theme.color.brand else Theme.color.inkMain,
                    label = "page indicator color"
                )
                Box(
                    modifier = Modifier
                        .padding(Theme.spacing.sizeXXS)
                        .clip(CircleShape)
                        .background(indicatorColor)
                        .size(Theme.size.sizeS)
                )
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.sizeL))

        // Title and Description
        Text(
            text = currentPageData.title,
            style = Theme.typography.title,
            color = Theme.color.inkMain,
            textAlign = TextAlign.Start,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Theme.spacing.sizeS))
        Text(
            text = currentPageData.description,
            style = Theme.typography.body,
            color = Theme.color.inkMain,
            textAlign = TextAlign.Start,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(0.2f))

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrimaryButton(
                text = stringResource(id = R.string.wizard_button_previous),
                onClick = {
                    coroutineScope.launch {
                        onAction(WizardPagerAction.OnBackClick)
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                enabled = pagerState.currentPage > 0,
                sizes = PrimaryButtonDefault.buttonSizeSet().buttonSize48()
            )
            PrimaryButton(
                text = if (pagerState.currentPage == pages.size - 1) stringResource(id = R.string.wizard_button_finish) else stringResource(
                    id = R.string.wizard_button_next
                ),
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage == pages.size - 1) {
                            onAction(WizardPagerAction.OnFinishClick)
                        } else {
                            onAction(WizardPagerAction.OnNextClick)
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                enabled = currentPageData.isNextEnabled(), // Use the new condition
                sizes = PrimaryButtonDefault.buttonSizeSet().buttonSize48()
            )
        }
    }
}

@Preview
@Composable
private fun WizardPagerPreview() {
    val samplePages = listOf(
        WizardPageData(
            "Welcome!",
            "This is the first page.",
            Color.LightGray,
            isNextEnabled = { false }) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Content 1") }
        },
        WizardPageData(
            "Features",
            "Discover our new features.",
            Color.Cyan,
            isNextEnabled = { true }) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Content 2") }
        },
        WizardPageData(
            "Get Started",
            "You are ready to go.",
            Color.Yellow,
            isNextEnabled = { true }) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Content 3") }
        }
    )
    AppTheme {
        WizardPager(pages = samplePages, onAction = { action ->
            when (action) {
                WizardPagerAction.OnFinishClick -> {}
                WizardPagerAction.OnNextClick -> {}
                WizardPagerAction.OnBackClick -> {}
            }
        })
    }
}