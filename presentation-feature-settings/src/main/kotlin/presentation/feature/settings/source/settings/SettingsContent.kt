package presentation.feature.settings.source.settings

import IcClientId
import IcPassword24
import IcPort24
import IcUsername24
import android.os.Build
import android.util.Log
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.MqttModel
import domain.core.source.model.ThemeModel
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
import presentation.core.ui.source.kit.atom.icon.IcOpen24
import presentation.core.ui.source.kit.atom.icon.IcRefresh24
import presentation.core.ui.source.kit.atom.icon.IcSensor24
import presentation.core.ui.source.kit.atom.icon.IcTheme24
import presentation.core.ui.source.kit.atom.icon.IcTimeout24
import presentation.core.ui.source.kit.atom.icon.IcWeb24
import presentation.core.ui.source.kit.atom.icon.IcWebProtected24
import presentation.core.ui.source.kit.atom.item.SectionItem
import presentation.core.ui.source.kit.atom.item.SectionToggleItem
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
 * sub-composable. It also manages the overall layout, scrolling, and theme reveal animations.
 *
 * @param state The current state of the settings.
 * @param onIntent Callback to handle user actions.
 * @param showLanguageDialog Whether the in-app language dialog is visible.
 * @param onShowLanguageDialogChange Callback to toggle language dialog visibility.
 * @param snackbarHostState State for displaying snackbars.
 */
@Composable
internal fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    showLanguageDialog: Boolean,
    onShowLanguageDialogChange: (Boolean) -> Unit,
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState()
) {
    val scrollState = rememberScrollState()

    SafeContainer(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient())
            .padding(top = Theme.spacing.sizeL),
        snackbarHostState = snackbarHostState
    ) {
        if (state.theme != null) {
            CircularReveal(
                targetState = state.theme,
                animationSpec = tween(500),
            ) { circularTheme ->
                AppTheme(mode = circularTheme) {
                    AnimationSequenceHost {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundGradient())
                        ) {
                            AnimatedItem(
                                index = 0,
                                enter = slideInVertically(tween(250)) { -it }
                            ) {
                                SettingsHeader(
                                    modifier = Modifier.padding(horizontal = Theme.spacing.sizeL, vertical = Theme.spacing.sizeL),
                                    title = stringResource(R.string.settings_title)
                                ) { action ->
                                    when (action) {
                                        SettingsHeaderAction.OnBackClick -> onIntent(SettingsIntent.OnBackIntent)
                                        SettingsHeaderAction.OnMoreClick -> onIntent(SettingsIntent.OnMoreIntent)
                                    }
                                }
                            }

                            AnimatedItem(
                                index = 1,
                                enter = slideInVertically(tween(250)) { it }
                            ) {
                                HorizontalAnimatedDivider(isVisible = scrollState.canScrollBackward)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(horizontal = Theme.spacing.sizeL)
                                        .padding(top = 2.dp)
                                        .verticalScroll(scrollState),
                                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL),
                                ) {
                                    var isDashboardValid by remember { mutableStateOf(false) }

                                    MoveDetectorSection(state, onIntent)
                                    MqttSection(state, onIntent, isDashboardValid)
                                    WebKioskSection(state, onIntent) { isDashboardValid = it }
                                    UiUxSection(state, onIntent)
                                    SystemSection(state, onIntent)

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
                            onDismiss = { onShowLanguageDialogChange(false) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section for configuring the camera-based motion detector.
 */
@Composable
private fun MoveDetectorSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val moveDetector = state.moveDetector ?: MoveDetectorModel(
        enabled = true,
        sensitivity = 50,
        dimDelay = 30L,
        screenTimeout = 60L,
        fabDelay = 60L
    )

    val isToggleAllowed = (moveDetector.sensitivity ?: 0) != 0 &&
            (moveDetector.dimDelay ?: 0L) != 0L &&
            (moveDetector.screenTimeout ?: 0L) != 0L &&
            (moveDetector.fabDelay ?: 0L) != 0L

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_move_detector),
            checked = moveDetector.enabled ?: false,
            enabled = isToggleAllowed,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(enabled = isEnabled)))
            }
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_sensitivity),
            icon = IcSensor24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.sensitivity ?: 0,
            onValueChange = { onIntent(SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(sensitivity = it))) },
            range = 0..100,
            enabled = true
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_dim_delay),
            icon = IcDim24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.dimDelay?.toInt() ?: 0,
            onValueChange = { onIntent(SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(dimDelay = it.toLong()))) },
            range = 0..300,
            enabled = true
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_screen_timeout),
            icon = IcTimeout24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.screenTimeout?.toInt() ?: 0,
            onValueChange = { onIntent(SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(screenTimeout = it.toLong()))) },
            range = 0..3600,
            enabled = true
        )

        NumberInputListItem(
            text = stringResource(R.string.settings_fab_delay),
            icon = IcOpen24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            value = moveDetector.fabDelay?.toInt() ?: 0,
            onValueChange = { onIntent(SettingsIntent.OnSetMoveDetectorIntent(moveDetector.copy(fabDelay = it.toLong()))) },
            range = 0..3600,
            enabled = true
        )
    }
}

/**
 * Section for configuring the MQTT telemetry and control broker connection.
 */
@Composable
private fun MqttSection(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    isDashboardValid: Boolean
) {
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
            onIntent(SettingsIntent.OnSetMqttIntent(
                (state.mqtt ?: MqttModel()).copy(
                    ip = mqttIp,
                    port = mqttPort,
                    clientId = mqttClientId,
                    username = mqttUsername,
                    password = mqttPassword,
                    friendlyName = mqttFriendlyName
                )
            ))
        }
    }

    // Regex patterns for validation (from architectural requirements)
    val ipRegex = remember { Regex("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$|^(?!:\\/\\/)([a-zA-Z0-9-_]+\\.?)*[a-zA-Z0-9][a-zA-Z0-9-_]*$") }
    val portRegex = remember { Regex("^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$") }
    val commonRegex = remember { Regex("^[a-zA-Z0-9_\\-\\.\\s]{1,64}$") }

    // Toggle eligibility: mandatory MQTT fields + Dashboard validity must match Regex.
    val isMqttConfigValid = isDashboardValid &&
            ipRegex.matches(mqttIp) &&
            portRegex.matches(mqttPort) &&
            commonRegex.matches(mqttClientId) &&
            commonRegex.matches(mqttUsername) &&
            mqttPassword.isNotEmpty() &&
            commonRegex.matches(mqttFriendlyName)

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionToggleItem(
            title = stringResource(R.string.settings_mqtt),
            checked = state.mqtt?.enabled ?: false,
            enabled = isMqttConfigValid,
            onCheckedChange = { isEnabled ->
                onIntent(SettingsIntent.OnSetMqttIntent((state.mqtt ?: MqttModel()).copy(enabled = isEnabled)))
            }
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text, autoCorrect = false)
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number)
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Ascii, autoCorrect = false)
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Ascii, autoCorrect = false)
        )
        PasswordInputListItem(
            initialText = mqttPassword,
            onTextChanged = { mqttPassword = it },
            placeholder = stringResource(R.string.settings_mqtt_password),
            icon = IcPassword24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            enabled = true
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text)
        )
    }
}

/**
 * Section for configuring the web kiosk dashboard and whitelist.
 */
@Composable
private fun WebKioskSection(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onValidationChange: (Boolean) -> Unit
) {
    var dashboardUrl by remember { mutableStateOf(state.dashboardUrls?.dashboardUrl ?: "http://") }
    var whitelistUrl by remember { mutableStateOf(state.dashboardUrls?.whitelistUrl ?: "http://") }

    LaunchedEffect(state.dashboardUrls) {
        state.dashboardUrls?.let {
            dashboardUrl = it.dashboardUrl
            whitelistUrl = it.whitelistUrl
        }
    }

    LaunchedEffect(dashboardUrl, whitelistUrl) {
        if (!state.isLoading) {
            kotlinx.coroutines.delay(1000)
            onIntent(SettingsIntent.OnSetDashboardIntent(dashboardUrl, whitelistUrl))
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Uri, autoCorrect = false)
        )
        TextInputListItem(
            initialText = whitelistUrl,
            onTextChanged = { whitelistUrl = it },
            placeholder = stringResource(R.string.url_configuration_whitelist_placeholder),
            icon = IcWebProtected24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            validationRegex = whitelistRegex,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Uri, autoCorrect = false)
        )
        SimpleListItem(
            text = stringResource(R.string.settings_application),
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            icon = IcApp24,
        ) {
            onIntent(SettingsIntent.OnApplicationIntent)
        }
    }
}

/**
 * Section for visual settings like theme and dock position.
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
            selectedTheme = when (state.theme) {
                ThemeModel.Dark -> ThemeOption.Dark
                ThemeModel.MaterialU -> ThemeOption.MaterialU
                else -> ThemeOption.Light
            },
            isMaterialUAvailable = isMaterialUAvailable
        ) { option ->
            val newTheme = when (option) {
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
            selectedSide = when (state.dock?.position) {
                DockPositionModel.Position.Up -> DockPosition.Up
                else -> DockPosition.Left
            }
        ) { option ->
            onIntent(SettingsIntent.OnSetDockIntent(DockPositionModel(
                when (option) {
                    DockPosition.Up -> DockPositionModel.Position.Up
                    DockPosition.Left -> DockPositionModel.Position.Left
                }
            )))
        }

        LanguageListItem(
            text = stringResource(R.string.settings_lang),
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            icon = IcLang24,
            selectedLanguageCode = (state.currentLanguage ?: currentTag).uppercase(java.util.Locale.getDefault()),
            onLanguageChange = { onIntent(SettingsIntent.OnLangIntent) }
        )
    }
}

/**
 * Section for system-level actions like restarting or viewing version info.
 */
@Composable
private fun SystemSection(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val context = LocalContext.current
    val packageInfo = remember(context) { context.packageManager.getPackageInfo(context.packageName, 0) }
    val versionDisplay = "v${packageInfo.versionName} (${packageInfo.versionCode})"

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL)) {
        SectionItem(title = stringResource(R.string.settings_system))

        SimpleListItem(
            text = stringResource(R.string.settings_restart),
            iconBackgroundColor = Theme.color.error,
            iconForegroundColor = Theme.color.surface,
            icon = IcRefresh24,
        ) {
            onIntent(SettingsIntent.OnRestartIntent)
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing.sizeM, bottom = Theme.spacing.sizeL),
            text = versionDisplay,
            style = Theme.typography.caption,
            color = Theme.color.inkMain.copy(alpha = 0.3f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * A dialog for selecting the application language in-app.
 */
@Composable
public fun LanguageSelectionDialog(
    showDialog: Boolean,
    currentLanguage: String?,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        val languages = listOf(
            java.util.Locale.ENGLISH.toLanguageTag(),
            java.util.Locale("uk").toLanguageTag()
        )
        val languageNames = mapOf(
            java.util.Locale.ENGLISH.toLanguageTag() to stringResource(R.string.lang_english),
            java.util.Locale("uk").toLanguageTag() to stringResource(R.string.lang_ukrainian)
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.settings_lang_select)) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageSelected(lang) }
                                .padding(Theme.spacing.sizeM),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = lang == currentLanguage,
                                onClick = { onLanguageSelected(lang) }
                            )
                            Text(
                                text = languageNames[lang] ?: lang,
                                modifier = Modifier.padding(start = Theme.spacing.sizeM)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
