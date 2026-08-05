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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import presentation.core.localisation.R
import presentation.core.styling.core.FormFactor
import presentation.core.styling.core.LocalFormFactor
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
 * It is a stateless composable that receives its state and dispatches user actions
 * via the [onIntent] callback.
 *
 * @param state The current [AboutState] providing UI information.
 * @param onIntent Callback for dispatching user intents back to the ViewModel. Supported
 *   actions include: [AboutIntent.OnBackIntent] for navigating back,
 *   [AboutIntent.OnGitHubIntent] for opening the GitHub link,
 *   [AboutIntent.OnLinkedInIntent] for opening the LinkedIn link,
 *   and [AboutIntent.OnTwitterIntent] for opening the Twitter link.
 * @see AboutScreen
 * @see AboutViewModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun AboutContent(state: AboutState, onIntent: (AboutIntent) -> Unit) {
    val isTv = LocalFormFactor.current == FormFactor.TV
    // On TV this screen is read from across the room: cap the content to a readable measure and
    // centre it rather than stretching a lone column across the whole 16:9 panel. On mobile it
    // stays full-width. Inset for overscan so nothing lands in the bezel dead zone.
    val overscan = if (isTv) Theme.spacing.sizeL else 0.dp
    SafeContainer(
        modifier =
        Modifier
            .fillMaxSize(),
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
                    // Full-width header row pins the back button hard-left; without it the animated
                    // node is only button-width and can drift horizontally during the slide-in.
                    Row(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            icon = IcArrowLeft24,
                            onClick = { onIntent(AboutIntent.OnBackIntent) },
                            sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
                            colors = IconButtonDefault.buttonColor(), // Default is Primary (Brand)
                        )
                    }
                }
                AnimatedItem(
                    // Header stays full-width; only the body is capped and centred on TV.
                    modifier =
                    if (isTv) {
                        Modifier
                            .widthIn(max = ABOUT_TV_MAX_WIDTH)
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

// Caps the About content width on TV so text and logo stay a comfortable reading measure.
private val ABOUT_TV_MAX_WIDTH = 720.dp

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

@Preview(name = "TV", device = Devices.TV_1080p, showBackground = true)
@Composable
private fun AboutContentTvPreview() {
    CompositionLocalProvider(LocalFormFactor provides FormFactor.TV) {
        AppTheme {
            AboutContent(
                state =
                AboutState(
                    isLoading = false,
                ),
            ) { }
        }
    }
}
