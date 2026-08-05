package presentation.feature.settings.source.settings

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import presentation.core.localisation.R
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.divider.HorizontalAnimatedDivider
import presentation.core.ui.source.kit.atom.gradient.backgroundGradient
import presentation.core.ui.source.kit.molecule.header.SettingsHeader
import presentation.core.ui.source.kit.molecule.header.SettingsHeaderAction
import presentation.core.ui.source.kit.organism.animatedsequence.AnimatedItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimationSequenceHost

/**
 * The mobile (phone/tablet) settings layout: a single vertically-scrolling column of every
 * section, wrapped in the staggered entry animation ([AnimationSequenceHost]).
 *
 * All section composables ([MoveDetectorSection], [MqttSection], [WebKioskSection], …) are shared
 * verbatim with [SettingsTvContent]; only the surrounding layout (scroll column vs list-detail)
 * differs between form factors.
 *
 * @param state The current [SettingsState].
 * @param onIntent Callback to dispatch [SettingsIntent]s.
 * @param overscan Horizontal inset (0.dp on mobile; kept as a parameter so both layouts share one
 *   source of truth for the value computed in [SettingsContent]).
 * @see SettingsContent
 * @see SettingsTvContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 1.4.0
 */
// Suppressed: deeply nested Compose layout makes formatter indentation unreliable.
@Suppress("Indentation")
@Composable
internal fun SettingsMobileContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    overscan: Dp,
) {
    val scrollState = rememberScrollState()

    AnimationSequenceHost {
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .background(backgroundGradient())
                .padding(horizontal = overscan),
        ) {
            AnimatedItem(
                index = 0,
                enter = slideInVertically(tween(250)) { -it },
            ) {
                SettingsHeader(
                    modifier =
                    Modifier.padding(
                        horizontal = Theme.spacing.sizeL,
                        vertical = Theme.spacing.sizeL,
                    ),
                    title = stringResource(R.string.settings_title),
                ) { action ->
                    when (action) {
                        SettingsHeaderAction.OnBackClick -> onIntent(SettingsIntent.OnBackIntent)
                        SettingsHeaderAction.OnMoreClick -> onIntent(SettingsIntent.OnMoreIntent)
                    }
                }
            }

            AnimatedItem(
                index = 1,
                enter = slideInVertically(tween(250)) { it },
            ) {
                HorizontalAnimatedDivider(isVisible = scrollState.canScrollBackward)
                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Theme.spacing.sizeL)
                        .padding(top = 2.dp)
                        .focusGroup()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL),
                ) {
                    var isDashboardValid by remember { mutableStateOf(false) }

                    MoveDetectorSection(state, onIntent)
                    StreamingSection(state, onIntent)
                    ScreensaverSection(state, onIntent)
                    AutoRebootSection(state, onIntent)
                    MqttSection(state, onIntent, isDashboardValid)
                    WebKioskSection(state, onIntent) { isDashboardValid = it }
                    WebViewRefreshSection(state, onIntent)
                    UiUxSection(state, onIntent)
                    SystemSection(state, onIntent)
                    AdvancedSection(onIntent)

                    val context = LocalContext.current
                    val packageInfo = remember(context) {
                        context.packageManager.getPackageInfo(context.packageName, 0)
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Theme.spacing.sizeM),
                        text = "v${packageInfo.versionName} (${packageInfo.versionCode})",
                        style = Theme.typography.caption,
                        color = Theme.color.inkMain.copy(alpha = 0.3f),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(Theme.spacing.sizeL))
                }
            }
        }
    }
}

/**
 * Renders [SettingsMobileContent] with the shared [previewSettingsState] and no-op callbacks.
 *
 * The mobile layout reads no form factor of its own — it is only ever the mobile branch — so the
 * preview calls it directly with a zero overscan inset.
 */
@Composable
private fun SettingsMobileContentPreview() {
    AppTheme {
        SettingsMobileContent(
            state = previewSettingsState,
            onIntent = {},
            overscan = 0.dp,
        )
    }
}

/**
 * Phone preview of the mobile settings layout (compact width, touch input).
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 1.4.0
 */
@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Composable
private fun SettingsMobileContentPhonePreview() {
    SettingsMobileContentPreview()
}

/**
 * Tablet preview of the mobile settings layout (expanded width, touch input).
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 1.4.0
 */
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Composable
private fun SettingsMobileContentTabletPreview() {
    SettingsMobileContentPreview()
}
