package presentation.feature.settings.source.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import presentation.core.localisation.R
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.gradient.backgroundGradient
import presentation.core.ui.source.kit.atom.icon.IcApp24
import presentation.core.ui.source.kit.atom.icon.IcCamera24
import presentation.core.ui.source.kit.atom.icon.IcDim24
import presentation.core.ui.source.kit.atom.icon.IcForward24
import presentation.core.ui.source.kit.atom.icon.IcRefresh24
import presentation.core.ui.source.kit.atom.icon.IcSensor24
import presentation.core.ui.source.kit.atom.icon.IcTheme24
import presentation.core.ui.source.kit.atom.icon.IcTimeout24
import presentation.core.ui.source.kit.atom.icon.IcWeb24
import presentation.core.ui.source.kit.atom.icon.IcWebProtected24
import presentation.core.ui.source.kit.molecule.header.SettingsHeader
import presentation.core.ui.source.kit.molecule.header.SettingsHeaderAction
import presentation.core.ui.source.kit.molecule.item.BaseListItem

// Master/detail split for the TV layout: a narrow section list beside a wider detail pane.
private const val SETTINGS_TV_MASTER_WEIGHT = 0.38f
private const val SETTINGS_TV_DETAIL_WEIGHT = 0.62f

/**
 * The setting sections rendered as rows in the TV master pane.
 *
 * Each entry maps one-to-one to an existing section composable rendered in the detail pane, so the
 * TV layout adds a navigation shell without duplicating any section content.
 *
 * @property titleRes Localized section title shown in the row.
 * @property icon Leading icon shown in the row.
 * @since 1.2.0
 */
private enum class SettingsSectionTab(val titleRes: Int, val icon: ImageVector) {
    MoveDetector(R.string.settings_move_detector, IcSensor24),
    Streaming(R.string.settings_streaming, IcCamera24),
    Screensaver(R.string.settings_screensaver, IcDim24),
    AutoReboot(R.string.settings_auto_reboot, IcTimeout24),
    Mqtt(R.string.settings_mqtt, IcWebProtected24),
    WebKiosk(R.string.settings_web_kiosk, IcWeb24),
    WebViewRefresh(R.string.settings_webview_refresh, IcRefresh24),
    UiUx(R.string.settings_ui_ux, IcTheme24),
    System(R.string.settings_system, IcApp24),
    Advanced(R.string.settings_advanced, IcForward24),
}

/**
 * The two-pane list-detail layout used on Android TV.
 *
 * The left pane is a D-pad-focusable list of sections; the right pane renders the selected section
 * in full, reusing the exact same section composables as the mobile layout — so auto-save,
 * validation, and debounce behave identically. Selection follows focus: moving the remote onto a
 * row immediately swaps the detail pane. D-pad right steps into the detail pane to edit; left
 * returns to the list via Compose spatial focus (hence the "Back to list" hint).
 *
 * @param state The current [SettingsState].
 * @param onIntent Callback to dispatch [SettingsIntent]s.
 * @param overscan Horizontal inset that keeps controls out of the TV overscan dead zone.
 * @see SettingsContent
 * @see SettingsMobileContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 1.2.0
 */
// Suppressed: deeply nested Compose layout makes formatter indentation unreliable.
@OptIn(ExperimentalFoundationApi::class)
@Suppress("Indentation")
@Composable
internal fun SettingsTvContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    overscan: Dp,
) {
    var selectedTab by rememberSaveable { mutableStateOf(SettingsSectionTab.MoveDetector) }

    // D-pad users have no cursor: without an explicit starting target the first remote press is
    // spent only pulling focus into the screen. Seed focus onto the section list once (see the
    // LaunchedEffect below).
    val listFocusRequester = remember { FocusRequester() }

    // The section list is a SINGLE focus target: the individual rows are non-focusable, so D-pad
    // focus can never leak off the list onto the detail pane while the user is just browsing. UP/DOWN
    // move the highlighted section via state; OK (or RIGHT) hands real focus to the detail pane.
    val detailFocusRequester = remember { FocusRequester() }
    // Per-row scroll anchors so the highlighted section is scrolled into view even though the rows
    // themselves are not focusable (focus-driven auto-scroll is unavailable here).
    val rowBringIntoView = remember { SettingsSectionTab.entries.associateWith { BringIntoViewRequester() } }

    LaunchedEffect(selectedTab) {
        runCatching { rowBringIntoView.getValue(selectedTab).bringIntoView() }
    }

    // The MQTT toggle is gated on dashboard validity. On mobile that flag is lifted out of
    // WebKioskSection, but here only one section is mounted at a time, so WebKioskSection may be
    // absent when MQTT is shown. Derive validity straight from state instead of via composition.
    val dashboardRegex = remember { Regex("^(https?://)?([\\da-z\\.-]+)(:[0-9]{1,5})?([/\\w \\.-]*)*\\/?$") }
    val whitelistRegex = remember { Regex("^([a-zA-Z0-9_\\-\\.\\s,]*)$") }
    val isDashboardValid =
        state.dashboardUrls?.let {
            dashboardRegex.matches(it.dashboardUrl) &&
                (it.whitelistUrl.isEmpty() || whitelistRegex.matches(it.whitelistUrl))
        } ?: false

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(backgroundGradient())
            .padding(horizontal = overscan),
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

        Row(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = Theme.spacing.sizeL),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL),
        ) {
            // Master pane: the whole section list is ONE focus target (`.focusable()`), and the rows
            // are plain non-focusable visuals. D-pad handling is explicit via onPreviewKeyEvent,
            // which runs before Compose's default spatial search — so focus physically cannot leak
            // onto the detail pane (or a detail text field) while the user browses the list. UP/DOWN
            // move the highlighted section through state; OK/RIGHT hand real focus to the detail
            // pane; LEFT is swallowed; UP off the first row is passed through so the header action
            // buttons stay reachable.
            Column(
                modifier =
                Modifier
                    .weight(SETTINGS_TV_MASTER_WEIGHT)
                    .fillMaxHeight()
                    .focusRequester(listFocusRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        val entries = SettingsSectionTab.entries
                        val index = entries.indexOf(selectedTab)
                        when (event.key) {
                            Key.DirectionDown -> {
                                if (index < entries.lastIndex) selectedTab = entries[index + 1]
                                true
                            }
                            Key.DirectionUp -> {
                                if (index > 0) {
                                    selectedTab = entries[index - 1]
                                    true
                                } else {
                                    false
                                }
                            }
                            Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.DirectionRight -> {
                                detailFocusRequester.requestFocus()
                                true
                            }
                            Key.DirectionLeft -> true
                            else -> false
                        }
                    }
                    .focusable()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeS),
            ) {
                SettingsSectionTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    BaseListItem(
                        // No onClick: the row is a non-focusable visual; selection and entry into the
                        // detail pane are driven entirely by the container's key handler above.
                        modifier = Modifier.bringIntoViewRequester(rowBringIntoView.getValue(tab)),
                        icon = tab.icon,
                        text = stringResource(tab.titleRes),
                        iconBackgroundColor = if (isSelected) Theme.color.brand else Theme.color.surfaceVariant,
                        iconForegroundColor = Theme.color.inkMain,
                    )
                }
            }

            // Detail pane: the full section, reused verbatim from the mobile layout.
            Column(
                modifier =
                Modifier
                    .weight(SETTINGS_TV_DETAIL_WEIGHT)
                    .fillMaxHeight()
                    .focusRequester(detailFocusRequester)
                    // LEFT out of the detail pane returns focus to the section list container.
                    .focusProperties { left = listFocusRequester }
                    .focusGroup()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL),
            ) {
                when (selectedTab) {
                    SettingsSectionTab.MoveDetector -> MoveDetectorSection(state, onIntent)
                    SettingsSectionTab.Streaming -> StreamingSection(state, onIntent)
                    SettingsSectionTab.Screensaver -> ScreensaverSection(state, onIntent)
                    SettingsSectionTab.AutoReboot -> AutoRebootSection(state, onIntent)
                    SettingsSectionTab.Mqtt -> MqttSection(state, onIntent, isDashboardValid)
                    SettingsSectionTab.WebKiosk -> WebKioskSection(state, onIntent) {}
                    SettingsSectionTab.WebViewRefresh -> WebViewRefreshSection(state, onIntent)
                    SettingsSectionTab.UiUx -> UiUxSection(state, onIntent)
                    SettingsSectionTab.System -> SystemSection(state, onIntent)
                    SettingsSectionTab.Advanced -> AdvancedSection(onIntent)
                }

                TvActionHints(modifier = Modifier.padding(top = Theme.spacing.sizeM))
                Spacer(modifier = Modifier.height(Theme.spacing.sizeL))
            }
        }
    }

    // Seed focus onto the section list so the first remote press edits rather than just enters.
    LaunchedEffect(Unit) {
        runCatching { listFocusRequester.requestFocus() }
    }
}

/**
 * A compact legend of D-pad affordances shown at the bottom of the TV detail pane.
 *
 * There is no explicit Save button: settings auto-save, matching the mobile behaviour.
 *
 * @param modifier Modifier applied to the row.
 * @since 1.2.0
 */
@Composable
private fun TvActionHints(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TvHintChip(key = "↕", label = stringResource(R.string.tv_hint_navigate))
        TvHintChip(key = "OK", label = stringResource(R.string.tv_hint_select))
        TvHintChip(key = "←", label = stringResource(R.string.tv_hint_back_to_list))
    }
}

/**
 * A single "key + label" chip within [TvActionHints].
 *
 * @param key The remote-key glyph (e.g. "OK", an arrow).
 * @param label What the key does.
 * @since 1.2.0
 */
@Composable
private fun TvHintChip(key: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeXS),
    ) {
        Text(
            modifier =
            Modifier
                .clip(RoundedCornerShape(Theme.spacing.sizeXS))
                .background(Theme.color.surfaceVariant)
                .padding(horizontal = Theme.spacing.sizeS, vertical = Theme.spacing.sizeXS),
            text = key,
            style = Theme.typography.caption,
            color = Theme.color.inkMain,
        )
        Text(
            text = label,
            style = Theme.typography.caption,
            color = Theme.color.inkMain.copy(alpha = 0.6f),
        )
    }
}

/**
 * TV preview of the list-detail settings layout (1080p leanback, D-pad input with overscan inset).
 *
 * Focus seeding and D-pad key handling are inert in a static preview; this only verifies the
 * two-pane visual layout.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 1.2.0
 */
@Preview(name = "TV", device = Devices.TV_1080p, showBackground = true)
@Composable
private fun SettingsTvContentPreview() {
    AppTheme {
        SettingsTvContent(
            state = previewSettingsState,
            onIntent = {},
            overscan = Theme.spacing.sizeL,
        )
    }
}
