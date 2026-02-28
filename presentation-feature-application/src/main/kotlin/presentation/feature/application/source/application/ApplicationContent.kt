package presentation.feature.application.source.application

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
 * @param onIntent Callback to dispatch user actions to the ViewModel.
 */
@Composable
internal fun ApplicationContent(
    state: ApplicationState,
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState(),
    onIntent: (ApplicationIntent) -> Unit,
) {
    val lazyColumnState = rememberLazyListState()


    SafeContainer(
        modifier = Modifier
            .fillMaxSize(),
        snackbarHostState = snackbarHostState
    ) {
        AnimationSequenceHost {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundGradient())
                    .padding(top = Theme.spacing.sizeL),
                horizontalAlignment = Alignment.Start
            ) {
                AnimatedItem(
                    index = 0,
                    enter = slideInVertically(
                        tween(
                            durationMillis = 250,
                        )
                    ) { fullHeight -> -fullHeight }
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(horizontal = Theme.spacing.sizeL),
                        icon = IcArrowLeft24,
                        onClick = { onIntent(ApplicationIntent.OnBackClick) },
                        sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
                        colors = IconButtonDefault.buttonColor()
                    )
                }
                AnimatedItem(
                    modifier = Modifier,
                    index = 1,
                    enter = slideInVertically(
                        tween(
                            durationMillis = 250,
                        )
                    ) { fullHeight -> +fullHeight }
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(Theme.spacing.sizeM))
                        HorizontalAnimatedDivider(
                            isVisible = lazyColumnState.canScrollBackward
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = Theme.spacing.sizeL),
                            state = lazyColumnState,
                            verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeS),
                            contentPadding = PaddingValues(bottom = Theme.spacing.sizeL)
                        ) {
                            items(state.data) { app ->
                                ApplicationListItem(
                                    applicationModel = app,
                                    onClick = {
                                        if (app.chosen == true) {
                                            onIntent(ApplicationIntent.RemoveApplication(app))
                                        } else {
                                            onIntent(ApplicationIntent.SaveApplication(app))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ApplicationContentPreview() {
    AppTheme {
        ApplicationContent(
            state = ApplicationState(
                isLoading = false
            )
        ) { }
    }
}