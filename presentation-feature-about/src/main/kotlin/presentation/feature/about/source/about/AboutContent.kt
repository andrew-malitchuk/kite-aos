package presentation.feature.about.source.about

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.localisation.R
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.core.ext.fillMaxSquare
import presentation.core.ui.source.kit.atom.button.icon.IconButton
import presentation.core.ui.source.kit.atom.button.icon.core.IconButtonDefault
import presentation.core.ui.source.kit.atom.container.SafeContainer
import presentation.core.ui.source.kit.atom.gradient.backgroundGradient
import presentation.core.ui.source.kit.atom.icon.IcArrowLeft24
import presentation.core.ui.source.kit.atom.icon.IcGitHub24
import presentation.core.ui.source.kit.atom.icon.IcLinkedIn24
import presentation.core.ui.source.kit.atom.icon.IcTwitter24
import presentation.core.ui.source.kit.organism.animatedsequence.AnimatedItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimationSequenceHost
import presentation.feature.about.core.composable.shape.AnimatedCookieShape

/**
 * The main UI content for the About screen.
 *
 * This Composable handles the layout and visual presentation of project information,
 * including animated transitions, project title/description, and social media icons.
 *
 * @param state The current [AboutState] providing UI information.
 * @param onIntent Callback for dispatching user intents back to the ViewModel.
 */
@Composable
internal fun AboutContent(state: AboutState, onIntent: (AboutIntent) -> Unit) {
    SafeContainer(
        modifier =
        Modifier
            .fillMaxSize(),
    ) {
        AnimationSequenceHost {
            Column(
                modifier =
                Modifier
                    .fillMaxSize()
                    .background(backgroundGradient())
                    .padding(Theme.spacing.sizeL),
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
                    IconButton(
                        icon = IcArrowLeft24,
                        onClick = { onIntent(AboutIntent.OnBackIntent) },
                        sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
                        colors = IconButtonDefault.buttonColor(), // Default is Primary (Brand)
                    )
                }
                AnimatedItem(
                    modifier = Modifier,
                    index = 1,
                    enter =
                    slideInVertically(
                        tween(
                            durationMillis = 250,
                        ),
                    ) { fullHeight -> +fullHeight },
                ) {
                    Column {
                        BoxWithConstraints(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            AnimatedCookieShape(
                                modifier = Modifier.fillMaxSquare(maxWidth, maxHeight),
                                color = Theme.color.brandVariant,
                            )
                            Image(
                                modifier = Modifier.fillMaxSquare(maxWidth, maxHeight),
                                painter = painterResource(presentation.core.ui.R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                            )
                        }

                        Text(
                            text = stringResource(id = R.string.about_title),
                            style = Theme.typography.title,
                            color = Theme.color.inkMain,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(Theme.spacing.sizeS))

                        Text(
                            text = stringResource(id = R.string.about_description),
                            style = Theme.typography.body,
                            color = Theme.color.inkMain,
                            textAlign = TextAlign.Start,
                            minLines = 3,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(Theme.spacing.sizeL))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                            Arrangement.spacedBy(
                                Theme.spacing.sizeL,
                                Alignment.Start,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                icon = IcGitHub24,
                                onClick = { onIntent(AboutIntent.OnGitHubIntent) },
                                sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
                                colors = IconButtonDefault.buttonColor(),
                            )
                            IconButton(
                                icon = IcLinkedIn24,
                                onClick = { onIntent(AboutIntent.OnLinkedInIntent) },
                                sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
                                colors = IconButtonDefault.buttonColor(),
                            )
                            IconButton(
                                icon = IcTwitter24,
                                onClick = { onIntent(AboutIntent.OnTwitterIntent) },
                                sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
                                colors = IconButtonDefault.buttonColor(),
                            )
                        }
                        Spacer(modifier = Modifier.height(Theme.spacing.sizeL))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AboutContentPreview() {
    AppTheme {
        AboutContent(
            state =
            AboutState(
                isLoading = false,
            ),
        ) { }
    }
}
