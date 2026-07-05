package presentation.feature.settings.source.settings

import IcClientId
import IcPassword24
import IcPort24
import IcUsername24
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import presentation.core.ui.source.kit.atom.button.icon.IconButton
import presentation.core.ui.source.kit.atom.button.icon.core.IconButtonDefault
import presentation.core.ui.source.kit.atom.icon.IcChrome24
import presentation.core.ui.source.kit.atom.icon.IcFirefox24
import presentation.core.ui.source.kit.molecule.item.BaseListItem
import domain.core.source.model.AutoRebootModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.HomeAssistantInstanceModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.MqttModel
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.ScreensaverSource
import domain.core.source.model.StreamingModel
import domain.core.source.model.ThemeModel
import domain.core.source.model.WebEngineModel
import domain.core.source.model.WebViewRefreshModel
import presentation.core.localisation.R
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.core.theme.CircularReveal
import presentation.core.ui.source.kit.atom.container.SafeContainer
import presentation.core.ui.source.kit.atom.divider.HorizontalAnimatedDivider
import presentation.core.ui.source.kit.atom.gradient.backgroundGradient
import presentation.core.ui.source.kit.atom.icon.IcApp24
import presentation.core.ui.source.kit.atom.icon.IcDim24
import presentation.core.ui.source.kit.atom.icon.IcDock24
import presentation.core.ui.source.kit.atom.icon.IcLang24
import presentation.core.ui.source.kit.atom.icon.IcForward24
import presentation.core.ui.source.kit.atom.icon.IcOpen24
import presentation.core.ui.source.kit.atom.icon.IcRefresh24
import presentation.core.ui.source.kit.atom.icon.IcCamera24
import presentation.core.ui.source.kit.atom.icon.IcSensor24
import presentation.core.ui.source.kit.atom.icon.IcTheme24
import presentation.core.ui.source.kit.atom.icon.IcTimeout24
import presentation.core.ui.source.kit.atom.icon.IcWeb24
import presentation.core.ui.source.kit.atom.icon.IcWebProtected24
import presentation.core.ui.source.kit.atom.item.SectionItem
import presentation.core.ui.source.kit.atom.item.SectionToggleItem
import presentation.core.ui.source.kit.molecule.item.ToggleListItem
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.header.SettingsHeader
import presentation.core.ui.source.kit.molecule.header.SettingsHeaderAction
import presentation.core.ui.source.kit.molecule.item.DockPosition
import presentation.core.ui.source.kit.molecule.item.LanguageListItem
import presentation.core.ui.source.kit.molecule.item.NumberInputListItem
import presentation.core.ui.source.kit.molecule.item.PasswordInputListItem
import presentation.core.ui.source.kit.molecule.item.PositionListItem
import presentation.core.ui.source.kit.molecule.item.SimpleListItem
import presentation.core.ui.source.kit.molecule.item.TextInputListItem
import presentation.core.ui.source.kit.molecule.item.ThemeListItem
import presentation.core.ui.source.kit.molecule.item.ThemeOption
import presentation.core.ui.source.kit.organism.animatedsequence.AnimatedItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimationSequenceHost

/**
 * The main content of the settings screen.
 *
 * This Composable orchestrates several sections of settings, each handled by its own
 * sub-composable. It also manages the overall layout, scrolling, and theme reveal animations
 * via [CircularReveal].
 *
 * @param state The current [SettingsState] of the settings screen.
 * @param onIntent Callback to handle user actions. Accepts all [SettingsIntent] subtypes.
 * @param showLanguageDialog Whether the in-app language dialog is visible.
 * @param onShowLanguageDialogChange Callback to toggle language dialog visibility.
 * @param snackbarHostState State for displaying snackbars.
 * @see SettingsScreen
 * @see SettingsViewModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
// Suppressed: deeply nested Compose layout makes formatter indentation unreliable.
@Suppress("Indentation")
@Composable
internal fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    showLanguageDialog: Boolean,
    onShowLanguageDialogChange: (Boolean) -> Unit,
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState(),
    showDiscoveryDialog: Boolean = false,
    discoveryResults: List<HomeAssistantInstanceModel> = emptyList(),
    onShowDiscoveryDialogChange: (Boolean) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    SafeContainer(
        modifier =
        Modifier
            .fillMaxSize()
            .background(backgroundGradient())
            .padding(top = Theme.spacing.sizeL),
        snackbarHostState = snackbarHostState,
    ) {
        if (state.theme != null) {
            CircularReveal(
                targetState = state.theme,
                animationSpec = tween(500),
            ) { circularTheme ->
                AppTheme(mode = circularTheme) {
                    AnimationSequenceHost {
                        Column(
                            modifier =
                            Modifier
                                .fillMaxSize()
                                .background(backgroundGradient()),
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

                    if (showLanguageDialog) {
                        LanguageSelectionDialog(
                            showDialog = showLanguageDialog,
                            currentLanguage = state.currentLanguage,
                            onLanguageSelected = { localeCode ->
                                onIntent(SettingsIntent.OnSetApplicationLanguageIntent(localeCode))
                                onShowLanguageDialogChange(false)
                            },
                            onDismiss = { onShowLanguageDialogChange(false) },
                        )
                    }

                    if (showDiscoveryDialog) {
                        HomeAssistantDiscoveryDialog(
                            instances = discoveryResults,
                            onSelect = { url ->
                                onIntent(SettingsIntent.OnSelectDiscoveredInstanceIntent(url))
                                onShowDiscoveryDialogChange(false)
                            },
                            onDismiss = { onShowDiscoveryDialogChange(false) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section for configuring the camera-based motion detector.
 *
 * Includes toggle, sensitivity, dim delay, screen timeout, and FAB delay controls.
 *
 * @param state The current [SettingsState] for reading motion detector configuration.
 * @param onIntent Callback to dispatch [SettingsIntent.OnSetMoveDetectorIntent] updates.
 * @since 0.0.1
 */
// Suppressed: deeply nested Compose layout makes formatter indentation unreliable.
@Suppress("Indentation")
@Composable
private fun MoveDetectorSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val moveDetector =
        state.moveDetector ?: MoveDetectorModel(
            enabled = true,
            sensitivity = 50,
            dimDelay = 30L,
            screenTimeout = 60L,
            fabDelay = 60L,
        )

    val isToggleAllowed =
        (moveDetector.sensitivity ?: 0) != 0 &&
            (moveDetector.dimDelay ?: 0L) != 0L &&
            (moveDetector.screenTimeout ?: 0L) != 0L &&
            (moveDetector.fabDelay ?: 0L) != 0L

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_move_detector),
            subtitle = stringResource(R.string.hint_move_detector),
            checked = moveDetector.enabled ?: false,
            enabled = isToggleAllowed,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(enabled = isEnabled)))
            },
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_sensitivity),
            subtitle = stringResource(R.string.hint_sensitivity),
            icon = IcSensor24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.sensitivity ?: 0,
            onValueChange = { onIntent(SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(sensitivity = it))) },
            range = 0..100,
            enabled = true,
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_dim_delay),
            subtitle = stringResource(R.string.hint_dim_delay),
            icon = IcDim24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.dimDelay?.toInt() ?: 0,
            onValueChange = {
                onIntent(
                    SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(dimDelay = it.toLong())),
                )
            },
            range = 0..300,
            enabled = true,
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_screen_timeout),
            subtitle = stringResource(R.string.hint_screen_timeout),
            icon = IcTimeout24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.screenTimeout?.toInt() ?: 0,
            onValueChange = {
                onIntent(
                    SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(screenTimeout = it.toLong())),
                )
            },
            range = 0..3600,
            enabled = true,
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_fab_delay),
            subtitle = stringResource(R.string.hint_fab_delay),
            icon = IcOpen24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.fabDelay?.toInt() ?: 0,
            onValueChange = {
                onIntent(
                    SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(fabDelay = it.toLong())),
                )
            },
            range = 0..3600,
            enabled = true,
        )
    }
}

/**
 * Section for configuring the MJPEG camera streaming server.
 *
 * Includes a toggle, port, quality, and FPS controls.
 *
 * @param state The current [SettingsState] for reading streaming configuration.
 * @param onIntent Callback to dispatch [SettingsIntent.OnSetStreamingIntent] updates.
 * @since 0.0.7
 */
@Composable
private fun StreamingSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val streaming = state.streaming ?: StreamingModel(
        enabled = false,
        port = 8080,
        quality = 75,
        fps = 10,
        rotation = 0,
    )

    val portRegex = remember {
        Regex("^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")
    }
    var streamingPort by remember { mutableStateOf(streaming.port?.toString() ?: "8080") }

    LaunchedEffect(state.streaming) {
        state.streaming?.let { streamingPort = it.port?.toString() ?: "8080" }
    }

    LaunchedEffect(streamingPort) {
        if (!state.isLoading) {
            kotlinx.coroutines.delay(1000)
            val port = streamingPort.toIntOrNull() ?: return@LaunchedEffect
            onIntent(SettingsIntent.OnSetStreamingIntent(streaming.copy(port = port)))
        }
    }

    val isToggleAllowed = portRegex.matches(streamingPort)

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_streaming),
            subtitle = stringResource(R.string.hint_streaming_section),
            checked = streaming.enabled ?: false,
            enabled = isToggleAllowed,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetStreamingIntent(streaming.copy(enabled = isEnabled)))
            },
        )

        TextInputListItem(
            initialText = streamingPort,
            onTextChanged = { streamingPort = it },
            placeholder = stringResource(R.string.settings_streaming_port),
            icon = IcPort24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true,
            validationRegex = portRegex,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_streaming_quality),
            subtitle = stringResource(R.string.hint_streaming_quality),
            icon = IcCamera24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = streaming.quality ?: 75,
            onValueChange = { onIntent(SettingsIntent.OnSetStreamingIntent(streaming.copy(quality = it))) },
            range = 1..100,
            enabled = true,
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_streaming_fps),
            subtitle = stringResource(R.string.hint_streaming_fps),
            icon = IcRefresh24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = streaming.fps ?: 10,
            onValueChange = { onIntent(SettingsIntent.OnSetStreamingIntent(streaming.copy(fps = it))) },
            range = 1..30,
            enabled = true,
        )

        val currentRotation = streaming.rotation ?: 0
        SimpleListItem(
            text = stringResource(R.string.settings_streaming_rotation, currentRotation),
            subtitle = stringResource(R.string.hint_streaming_rotation),
            icon = IcRefresh24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            onClick = {
                val nextRotation = (currentRotation + 90) % 360
                onIntent(SettingsIntent.OnSetStreamingIntent(streaming.copy(rotation = nextRotation)))
            },
        )
    }
}

/**
 * Section for configuring the screensaver overlay that appears during inactivity.
 *
 * Includes a toggle, activation delay, slide interval, clock toggle, and folder picker.
 *
 * @param state The current [SettingsState] for reading screensaver configuration.
 * @param onIntent Callback to dispatch [SettingsIntent.OnSetScreensaverIntent] and related updates.
 * @since 0.0.8
 */
@Composable
private fun ScreensaverSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val screensaver = state.screensaver ?: ScreensaverModel(
        enabled = false,
        activationDelay = 60L,
        slideInterval = 30L,
        showClock = true,
        source = ScreensaverSource.BLACK,
        localFolderUri = null,
    )

    val isToggleAllowed = (screensaver.activationDelay ?: 0L) > 0L

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_screensaver),
            subtitle = stringResource(R.string.hint_screensaver_section),
            checked = screensaver.enabled ?: false,
            enabled = isToggleAllowed,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetScreensaverIntent(screensaver.copy(enabled = isEnabled)))
            },
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_screensaver_activation_delay),
            subtitle = stringResource(R.string.hint_screensaver_activation_delay),
            icon = IcTimeout24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = screensaver.activationDelay?.toInt() ?: 60,
            onValueChange = {
                onIntent(SettingsIntent.OnSetScreensaverIntent(screensaver.copy(activationDelay = it.toLong())))
            },
            range = 0..3600,
            enabled = true,
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_screensaver_slide_interval),
            subtitle = stringResource(R.string.hint_screensaver_slide_interval),
            icon = IcRefresh24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = screensaver.slideInterval?.toInt() ?: 30,
            onValueChange = {
                onIntent(SettingsIntent.OnSetScreensaverIntent(screensaver.copy(slideInterval = it.toLong())))
            },
            range = 5..300,
            enabled = true,
        )

        ToggleListItem(
            text = stringResource(R.string.settings_screensaver_show_clock),
            icon = IcDim24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            isChecked = screensaver.showClock ?: true,
            onCheckedChange = {
                onIntent(SettingsIntent.OnSetScreensaverIntent(screensaver.copy(showClock = it)))
            },
        )

        SimpleListItem(
            text = if (!screensaver.localFolderUri.isNullOrEmpty()) {
                stringResource(R.string.settings_screensaver_folder_selected)
            } else {
                stringResource(R.string.settings_screensaver_pick_folder)
            },
            icon = IcOpen24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
        ) {
            onIntent(SettingsIntent.OnPickScreensaverFolderIntent)
        }
    }
}

@Composable
private fun AutoRebootSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val autoReboot = state.autoReboot ?: AutoRebootModel(
        enabled = false,
        hour = 3,
        minute = 0,
        intervalDays = 1,
    )

    val intervalCycle = listOf(1, 7, 14, 30)

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_auto_reboot),
            subtitle = stringResource(R.string.hint_auto_reboot_section),
            checked = autoReboot.enabled ?: false,
            enabled = true,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetAutoRebootIntent(autoReboot.copy(enabled = isEnabled)))
            },
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_auto_reboot_hour),
            icon = IcTimeout24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = autoReboot.hour ?: 3,
            onValueChange = {
                onIntent(SettingsIntent.OnSetAutoRebootIntent(autoReboot.copy(hour = it)))
            },
            range = 0..23,
            enabled = true,
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_auto_reboot_minute),
            icon = IcTimeout24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = autoReboot.minute ?: 0,
            onValueChange = {
                onIntent(SettingsIntent.OnSetAutoRebootIntent(autoReboot.copy(minute = it)))
            },
            range = 0..59,
            enabled = true,
        )

        val currentInterval = autoReboot.intervalDays ?: 1
        val nextInterval = intervalCycle[(intervalCycle.indexOf(currentInterval).takeIf { it >= 0 }
            ?.let { (it + 1) % intervalCycle.size } ?: 1)]
        SimpleListItem(
            text = stringResource(R.string.settings_auto_reboot_interval, currentInterval),
            subtitle = stringResource(R.string.hint_auto_reboot_interval),
            icon = IcRefresh24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            onClick = {
                onIntent(SettingsIntent.OnSetAutoRebootIntent(autoReboot.copy(intervalDays = nextInterval)))
            },
        )
    }
}

/**
 * Section for configuring the MQTT telemetry and control broker connection.
 *
 * Includes fields for IP, port, client ID, username, password, and friendly name.
 * A debounced [LaunchedEffect] auto-saves changes after 1 second of inactivity.
 *
 * @param state The current [SettingsState] for reading MQTT configuration.
 * @param onIntent Callback to dispatch [SettingsIntent.OnSetMqttIntent] updates.
 * @param isDashboardValid Whether the dashboard URL passes validation, used for MQTT toggle eligibility.
 * @since 0.0.1
 */
@Composable
private fun MqttSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit, isDashboardValid: Boolean) {
    var mqttIp by remember { mutableStateOf(state.mqtt?.ip ?: "") }
    var mqttPort by remember { mutableStateOf(state.mqtt?.port ?: "") }
    var mqttClientId by remember { mutableStateOf(state.mqtt?.clientId ?: "") }
    var mqttUsername by remember { mutableStateOf(state.mqtt?.username ?: "") }
    var mqttPassword by remember { mutableStateOf(state.mqtt?.password ?: "") }
    var mqttFriendlyName by remember { mutableStateOf(state.mqtt?.friendlyName ?: "") }

    LaunchedEffect(state.mqtt) {
        state.mqtt?.let {
            mqttIp = it.ip ?: ""
            mqttPort = it.port ?: ""
            mqttClientId = it.clientId ?: ""
            mqttUsername = it.username ?: ""
            mqttPassword = it.password ?: ""
            mqttFriendlyName = it.friendlyName ?: ""
        }
    }

    LaunchedEffect(mqttIp, mqttPort, mqttClientId, mqttUsername, mqttPassword, mqttFriendlyName) {
        if (!state.isLoading) {
            kotlinx.coroutines.delay(1000)
            onIntent(
                SettingsIntent.OnSetMqttIntent(
                    (state.mqtt ?: MqttModel()).copy(
                        ip = mqttIp,
                        port = mqttPort,
                        clientId = mqttClientId,
                        username = mqttUsername,
                        password = mqttPassword,
                        friendlyName = mqttFriendlyName,
                    ),
                ),
            )
        }
    }

    // Regex patterns for validation (from architectural requirements)
    // Suppressed: IP/hostname regex pattern necessarily exceeds line length.
    @Suppress("MaximumLineLength")
    val ipRegex =
        remember {
            Regex(
                "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$|^(?!:\\/\\/)([a-zA-Z0-9-_]+\\.?)*[a-zA-Z0-9][a-zA-Z0-9-_]*$",
            )
        }
    val portRegex =
        remember { Regex("^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$") }
    val commonRegex = remember { Regex("^[a-zA-Z0-9_\\-\\.\\s]{1,64}$") }

    // Toggle eligibility: mandatory MQTT fields + Dashboard validity must match Regex.
    val isMqttConfigValid =
        isDashboardValid &&
            ipRegex.matches(mqttIp) &&
            portRegex.matches(mqttPort) &&
            commonRegex.matches(mqttClientId) &&
            commonRegex.matches(mqttUsername) &&
            mqttPassword.isNotEmpty() &&
            commonRegex.matches(mqttFriendlyName)

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_mqtt),
            subtitle = stringResource(R.string.hint_mqtt_section),
            checked = state.mqtt?.enabled ?: false,
            enabled = isMqttConfigValid,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetMqttIntent((state.mqtt ?: MqttModel()).copy(enabled = isEnabled)))
            },
        )

        // Inputs always enabled (enabled = true) to allow configuration while MQTT is off.

        TextInputListItem(
            initialText = mqttIp,
            onTextChanged = { mqttIp = it },
            placeholder = stringResource(R.string.settings_mqtt_ip),
            icon = IcWeb24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true,
            validationRegex = ipRegex,
            keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                autoCorrect = false,
            ),
        )
        TextInputListItem(
            initialText = mqttPort,
            onTextChanged = { mqttPort = it },
            placeholder = stringResource(R.string.settings_mqtt_port),
            icon = IcPort24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true,
            validationRegex = portRegex,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
        )
        TextInputListItem(
            initialText = mqttClientId,
            onTextChanged = { mqttClientId = it },
            placeholder = stringResource(R.string.settings_mqtt_client_id),
            icon = IcClientId,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true,
            validationRegex = commonRegex,
            keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Ascii,
                autoCorrect = false,
            ),
        )
        TextInputListItem(
            initialText = mqttUsername,
            onTextChanged = { mqttUsername = it },
            placeholder = stringResource(R.string.settings_mqtt_username),
            icon = IcUsername24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true,
            validationRegex = commonRegex,
            keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Ascii,
                autoCorrect = false,
            ),
        )
        PasswordInputListItem(
            initialText = mqttPassword,
            onTextChanged = { mqttPassword = it },
            placeholder = stringResource(R.string.settings_mqtt_password),
            icon = IcPassword24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true,
        )
        TextInputListItem(
            initialText = mqttFriendlyName,
            onTextChanged = { mqttFriendlyName = it },
            placeholder = stringResource(R.string.settings_mqtt_friendly_name),
            icon = IcUsername24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true,
            validationRegex = commonRegex,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
        )
    }
}

/**
 * Section for configuring the web kiosk dashboard, whitelist URLs, and browser engine.
 *
 * Also provides a link to the application selection screen.
 *
 * @param state The current [SettingsState] for reading dashboard and engine configuration.
 * @param onIntent Callback to dispatch [SettingsIntent.OnSetDashboardIntent],
 *   [SettingsIntent.OnApplicationIntent], and [SettingsIntent.OnSetWebEngineIntent].
 * @param onValidationChange Callback reporting whether the dashboard URL passes validation,
 *   used by the MQTT section to determine toggle eligibility.
 * @since 0.0.1
 */
@Composable
private fun WebKioskSection(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onValidationChange: (Boolean) -> Unit,
) {
    var dashboardUrl by remember { mutableStateOf(state.dashboardUrls?.dashboardUrl ?: "http://") }
    var whitelistUrl by remember { mutableStateOf(state.dashboardUrls?.whitelistUrl ?: "http://") }
    var trustAllSsl by remember { mutableStateOf(state.dashboardUrls?.trustAllSsl ?: false) }

    LaunchedEffect(state.dashboardUrls) {
        state.dashboardUrls?.let {
            dashboardUrl = it.dashboardUrl
            whitelistUrl = it.whitelistUrl
            trustAllSsl = it.trustAllSsl
        }
    }

    LaunchedEffect(dashboardUrl, whitelistUrl, trustAllSsl) {
        if (!state.isLoading) {
            kotlinx.coroutines.delay(1000)
            onIntent(SettingsIntent.OnSetDashboardIntent(dashboardUrl, whitelistUrl, trustAllSsl))
        }
    }

    // Regex patterns for validation (from architectural requirements)
    val dashboardRegex = remember { Regex("^(https?://)?([\\da-z\\.-]+)(:[0-9]{1,5})?([/\\w \\.-]*)*\\/?$") }
    val whitelistRegex = remember { Regex("^([a-zA-Z0-9_\\-\\.\\s,]*)$") }

    // Report validation state: Dashboard URL is mandatory,
    // White List is optional but must match Regex if filled.
    LaunchedEffect(dashboardUrl, whitelistUrl) {
        val isDashboardOk = dashboardRegex.matches(dashboardUrl)
        val isWhitelistOk = whitelistUrl.isEmpty() || whitelistRegex.matches(whitelistUrl)
        onValidationChange(isDashboardOk && isWhitelistOk)
    }

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionItem(title = stringResource(R.string.settings_web_kiosk))

        TextInputListItem(
            initialText = dashboardUrl,
            onTextChanged = { dashboardUrl = it },
            placeholder = stringResource(R.string.url_configuration_dashboard_url_placeholder),
            icon = IcWeb24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            validationRegex = dashboardRegex,
            keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Uri,
                autoCorrect = false,
            ),
        )
        TextInputListItem(
            initialText = whitelistUrl,
            onTextChanged = { whitelistUrl = it },
            placeholder = stringResource(R.string.url_configuration_whitelist_placeholder),
            icon = IcWebProtected24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            validationRegex = whitelistRegex,
            keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Uri,
                autoCorrect = false,
            ),
        )
        SimpleListItem(
            text = if (state.isDiscovering) {
                stringResource(R.string.settings_discovering)
            } else {
                stringResource(R.string.settings_discover_ha)
            },
            icon = IcSensor24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
        ) {
            if (!state.isDiscovering) onIntent(SettingsIntent.OnDiscoverHomeAssistantIntent)
        }
        WebEngineListItem(
            currentEngine = state.webEngine,
            onEngineSelected = { engine -> onIntent(SettingsIntent.OnSetWebEngineIntent(engine)) },
        )
        SimpleListItem(
            text = stringResource(R.string.settings_application),
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            icon = IcApp24,
        ) {
            onIntent(SettingsIntent.OnApplicationIntent)
        }

        ToggleListItem(
            text = stringResource(R.string.settings_auto_return),
            subtitle = stringResource(R.string.hint_auto_return),
            icon = IcRefresh24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            isChecked = state.isAutoReturnEnabled,
            onCheckedChange = { onIntent(SettingsIntent.OnSetAutoReturnIntent(it)) },
        )
        ToggleListItem(
            text = stringResource(R.string.settings_trust_ssl),
            subtitle = stringResource(R.string.hint_trust_ssl),
            icon = IcWebProtected24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            isChecked = trustAllSsl,
            onCheckedChange = { trustAllSsl = it },
        )
    }
}

/**
 * Section for configuring periodic automatic WebView refresh.
 *
 * @param state The current [SettingsState] for reading WebView refresh configuration.
 * @param onIntent Callback to dispatch [SettingsIntent.OnSetWebViewRefreshIntent] updates.
 * @since 0.0.6
 */
@Composable
private fun WebViewRefreshSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val refresh = state.webViewRefresh ?: WebViewRefreshModel(enabled = false, intervalSeconds = 300L)

    val isToggleAllowed = (refresh.intervalSeconds ?: 0L) != 0L

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_webview_refresh),
            subtitle = stringResource(R.string.hint_webview_refresh),
            checked = refresh.enabled ?: false,
            enabled = isToggleAllowed,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetWebViewRefreshIntent(refresh.copy(enabled = isEnabled)))
            },
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_webview_refresh_interval),
            subtitle = stringResource(R.string.hint_webview_refresh_interval),
            icon = IcTimeout24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = refresh.intervalSeconds?.toInt() ?: 0,
            onValueChange = {
                onIntent(SettingsIntent.OnSetWebViewRefreshIntent(refresh.copy(intervalSeconds = it.toLong())))
            },
            range = 0..3600,
            enabled = true,
        )
    }
}

/**
 * Engine selector list item using the standard [BaseListItem] pattern with trailing [IconButton]s.
 *
 * Android WebView is represented by [IcChrome24]; GeckoView by [IcFirefox24].
 * The selected engine button is highlighted.
 *
 * @param currentEngine The currently selected [WebEngineModel].
 * @param onEngineSelected Callback invoked when the user picks a different engine.
 * @since 0.0.4
 */
@Composable
private fun WebEngineListItem(
    currentEngine: WebEngineModel,
    onEngineSelected: (WebEngineModel) -> Unit,
) {
    BaseListItem(
        icon = IcWeb24,
        text = stringResource(R.string.settings_web_engine_title),
        iconBackgroundColor = Theme.color.brand,
        iconForegroundColor = Theme.color.inkMain,
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeXS)) {
                IconButton(
                    icon = IcChrome24,
                    onClick = { onEngineSelected(WebEngineModel.AndroidWebView) },
                    sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                    isSelected = currentEngine == WebEngineModel.AndroidWebView,
                )
                val isGeckoViewAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                if (isGeckoViewAvailable) {
                    IconButton(
                        icon = IcFirefox24,
                        onClick = { onEngineSelected(WebEngineModel.GeckoView) },
                        sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                        isSelected = currentEngine == WebEngineModel.GeckoView,
                    )
                }
            }
        },
    )
}

/**
 * Section for visual settings like theme, dock position, and language.
 *
 * @param state The current [SettingsState] for reading UI/UX preferences.
 * @param onIntent Callback to dispatch [SettingsIntent.OnSetThemeIntent],
 *   [SettingsIntent.OnSetDockIntent], and [SettingsIntent.OnLangIntent].
 * @since 0.0.1
 */
@Composable
private fun UiUxSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val isMaterialUAvailable = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }
    val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionItem(title = stringResource(R.string.settings_ui_ux))

        ThemeListItem(
            text = stringResource(R.string.settings_theme),
            icon = IcTheme24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            selectedTheme =
            when (state.theme) {
                ThemeModel.Dark -> ThemeOption.Dark
                ThemeModel.MaterialU -> ThemeOption.MaterialU
                else -> ThemeOption.Light
            },
            isMaterialUAvailable = isMaterialUAvailable,
        ) { option ->
            val newTheme =
                when (option) {
                    ThemeOption.Light -> ThemeModel.Light
                    ThemeOption.Dark -> ThemeModel.Dark
                    ThemeOption.MaterialU -> ThemeModel.MaterialU
                }
            onIntent(SettingsIntent.OnSetThemeIntent(newTheme))
        }

        PositionListItem(
            text = stringResource(R.string.settings_dock_position),
            icon = IcDock24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            selectedSide =
            when (state.dock?.position) {
                DockPositionModel.Position.Up -> DockPosition.Up
                else -> DockPosition.Left
            },
        ) { option ->
            onIntent(
                SettingsIntent.OnSetDockIntent(
                    DockPositionModel(
                        when (option) {
                            DockPosition.Up -> DockPositionModel.Position.Up
                            DockPosition.Left -> DockPositionModel.Position.Left
                        },
                    ),
                ),
            )
        }

        LanguageListItem(
            text = stringResource(R.string.settings_lang),
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            icon = IcLang24,
            selectedLanguageCode = (state.currentLanguage ?: currentTag).uppercase(java.util.Locale.getDefault()),
            onLanguageChange = { onIntent(SettingsIntent.OnLangIntent) },
        )

        ToggleListItem(
            text = stringResource(R.string.settings_reduce_motion),
            subtitle = stringResource(R.string.hint_reduce_motion),
            icon = IcSensor24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            isChecked = state.isReduceMotionEnabled,
            onCheckedChange = { onIntent(SettingsIntent.OnSetReduceMotionIntent(it)) },
        )
    }
}

/**
 * Section for system-level actions like restarting or viewing version info.
 *
 * @param state The current [SettingsState] (unused but kept for consistency).
 * @param onIntent Callback to dispatch [SettingsIntent.OnRestartIntent].
 * @since 0.0.1
 */
@Composable
private fun SystemSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionItem(title = stringResource(R.string.settings_system))

        SimpleListItem(
            text = stringResource(R.string.settings_default_launcher),
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            icon = IcApp24,
        ) {
            onIntent(SettingsIntent.OnSetDefaultLauncherIntent)
        }

        SimpleListItem(
            text = stringResource(R.string.settings_restart),
            iconBackgroundColor = Theme.color.error,
            iconForegroundColor = Theme.color.surface,
            icon = IcRefresh24,
        ) {
            onIntent(SettingsIntent.OnRestartIntent)
        }
    }
}

/**
 * Section for advanced operations: configuration export and import.
 *
 * @param onIntent Callback to dispatch [SettingsIntent.OnExportConfigIntent] and [SettingsIntent.OnImportConfigIntent].
 * @since 0.0.5
 */
@Composable
private fun AdvancedSection(onIntent: (SettingsIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionItem(title = stringResource(R.string.settings_advanced))

        SimpleListItem(
            text = stringResource(R.string.settings_export_config),
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            icon = IcForward24,
        ) {
            onIntent(SettingsIntent.OnExportConfigIntent)
        }

        SimpleListItem(
            text = stringResource(R.string.settings_import_config),
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            icon = IcOpen24,
        ) {
            onIntent(SettingsIntent.OnImportConfigIntent)
        }
    }
}

/**
 * A dialog showing discovered Home Assistant instances for the user to select.
 *
 * Displays a list of instances with their URL and discovery source label.
 * An empty state message is shown when no instances were found.
 *
 * @param instances The list of discovered [HomeAssistantInstanceModel] instances.
 * @param onSelect Callback invoked with the URL when the user picks an instance.
 * @param onDismiss Callback invoked when the dialog is dismissed without selection.
 * @since 0.0.5
 */
@Composable
private fun HomeAssistantDiscoveryDialog(
    instances: List<HomeAssistantInstanceModel>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_discover_ha_dialog_title)) },
        text = {
            if (instances.isEmpty()) {
                Text(
                    text = stringResource(R.string.settings_discover_ha_empty),
                    style = Theme.typography.body,
                    color = Theme.color.inkMain.copy(alpha = 0.6f),
                )
            } else {
                Column {
                    instances.forEach { instance ->
                        val sourceLabel = when (instance.source) {
                            HomeAssistantInstanceModel.DiscoverySource.Quick ->
                                stringResource(R.string.settings_discover_ha_source_quick)
                            HomeAssistantInstanceModel.DiscoverySource.Scan ->
                                stringResource(R.string.settings_discover_ha_source_scan)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(instance.url) }
                                .padding(Theme.spacing.sizeM),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = instance.url,
                                    style = Theme.typography.body,
                                    color = Theme.color.inkMain,
                                )
                                Text(
                                    text = sourceLabel,
                                    style = Theme.typography.caption,
                                    color = Theme.color.inkMain.copy(alpha = 0.5f),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

/**
 * A dialog for selecting the application language in-app.
 *
 * Displays a list of supported languages (English, Ukrainian) as radio button options.
 *
 * @param showDialog Whether the dialog is currently visible.
 * @param currentLanguage The currently selected language code (e.g., "en", "uk"), or null.
 * @param onLanguageSelected Callback invoked with the chosen locale code when a language is selected.
 * @param onDismiss Callback invoked when the dialog is dismissed without selection.
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun LanguageSelectionDialog(
    showDialog: Boolean,
    currentLanguage: String?,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (showDialog) {
        val languages =
            listOf(
                java.util.Locale.ENGLISH.toLanguageTag(),
                java.util.Locale("uk").toLanguageTag(),
            )
        val languageNames =
            mapOf(
                java.util.Locale.ENGLISH.toLanguageTag() to stringResource(R.string.lang_english),
                java.util.Locale("uk").toLanguageTag() to stringResource(R.string.lang_ukrainian),
            )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.settings_lang_select)) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageSelected(lang) }
                                .padding(Theme.spacing.sizeM),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = lang == currentLanguage,
                                onClick = { onLanguageSelected(lang) },
                            )
                            Text(
                                text = languageNames[lang] ?: lang,
                                modifier = Modifier.padding(start = Theme.spacing.sizeM),
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}
