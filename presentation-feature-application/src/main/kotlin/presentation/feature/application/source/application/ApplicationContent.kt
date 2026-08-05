package presentation.feature.application.source.application

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.FormFactor
import presentation.core.styling.core.LocalFormFactor
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.icon.IconButton
import presentation.core.ui.source.kit.atom.button.icon.core.IconButtonDefault
import presentation.core.ui.source.kit.atom.container.SafeContainer
import presentation.core.ui.source.kit.atom.divider.HorizontalAnimatedDivider
import presentation.core.ui.source.kit.atom.gradient.backgroundGradient
import presentation.core.ui.source.kit.atom.icon.IcArrowLeft24
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.item.ApplicationListItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimatedItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimationSequenceHost

/**
 * The internal layout implementation for the Application Selection screen.
 *
 * It features a vertically animated layout using [AnimationSequenceHost]:
 * 1. A header row containing a back button.
 * 2. A scrollable list of applications with a visual divider that appears when scrolled.
 *
 * @param state The current UI state containing the list of applications.
 * @param snackbarHostState State for the Design System snackbar host.
 * @param onIntent Callback to dispatch user actions to the ViewModel. Supported actions include:
 *   [ApplicationIntent.OnBackClick] for navigating back,
 *   [ApplicationIntent.SaveApplication] for adding an app to the chosen list,
 *   and [ApplicationIntent.RemoveApplication] for removing an app from the chosen list.
 * @see ApplicationScreen
 * @see ApplicationViewModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun ApplicationContent(
    state: ApplicationState,
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState(),
    onIntent: (ApplicationIntent) -> Unit,
) {
    val lazyColumnState = rememberLazyListState()
    val isTv = LocalFormFactor.current == FormFactor.TV
    // On TV cap the list to a readable width and centre it instead of stretching rows across the
    // whole panel; inset for overscan. Mobile keeps the full-width layout untouched.
    val overscan = if (isTv) Theme.spacing.sizeL else 0.dp

    SafeContainer(
        modifier =
        Modifier
            .fillMaxSize(),
        snackbarHostState = snackbarHostState,
    ) {
        AnimationSequenceHost(
            modifier =
            Modifier
                .fillMaxSize()
                .background(backgroundGradient()),
        ) {
            Column(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = overscan)
                    .padding(top = Theme.spacing.sizeL),
                horizontalAlignment = Alignment.Start,
            ) {
                AnimatedItem(
                    index = 0,
                    enter =
                    slideInVertically(
                        tween(
                            durationMillis = 250,
                        ),
                    ) { fullHeight -> -fullHeight },
                ) {
                    // Full-width header row pins the back button hard-left; without it the animated
                    // node is only button-width and can drift horizontally during the slide-in.
                    Row(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            modifier =
                            Modifier
                                .padding(horizontal = Theme.spacing.sizeL),
                            icon = IcArrowLeft24,
                            onClick = { onIntent(ApplicationIntent.OnBackClick) },
                            sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
                            colors = IconButtonDefault.buttonColor(),
                        )
                    }
                }
                AnimatedItem(
                    // Header stays full-width; only the list body is capped and centred on TV.
                    modifier =
                    if (isTv) {
                        Modifier
                            .widthIn(max = APPLICATION_TV_MAX_WIDTH)
                            .align(Alignment.CenterHorizontally)
                    } else {
                        Modifier
                    },
                    index = 1,
                    enter =
                    slideInVertically(
                        tween(
                            durationMillis = 250,
                        ),
                    ) { fullHeight -> +fullHeight },
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(Theme.spacing.sizeM))
                        HorizontalAnimatedDivider(
                            isVisible = lazyColumnState.canScrollBackward,
                        )
                        LazyColumn(
                            modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = Theme.spacing.sizeL),
                            state = lazyColumnState,
                            verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeS),
                            contentPadding = PaddingValues(bottom = Theme.spacing.sizeL),
                        ) {
                            // Stable key = packageName. Selecting an app reorders the list
                            // (chosen items float to the top); with a key, Compose moves the same
                            // composable — and its D-pad focus — to the item's new position instead
                            // of leaving focus stranded on whatever now sits at the old index.
                            items(state.data, key = { it.packageName }) { app ->
                                ApplicationListItem(
                                    applicationModel = app,
                                    onClick = {
                                        if (app.chosen == true) {
                                            onIntent(ApplicationIntent.RemoveApplication(app))
                                        } else {
                                            onIntent(ApplicationIntent.SaveApplication(app))
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Caps the app-list width on TV so rows stay a comfortable reading measure instead of spanning
// the full panel.
private val APPLICATION_TV_MAX_WIDTH = 840.dp

@Preview
@Composable
private fun ApplicationContentPreview() {
    AppTheme {
        ApplicationContent(
            state =
            ApplicationState(
                isLoading = false,
            ),
        ) { }
    }
}

@Preview(name = "TV", device = Devices.TV_1080p, showBackground = true)
@Composable
private fun ApplicationContentTvPreview() {
    CompositionLocalProvider(LocalFormFactor provides FormFactor.TV) {
        AppTheme {
            ApplicationContent(
                state =
                ApplicationState(
                    isLoading = false,
                ),
            ) { }
        }
    }
}
