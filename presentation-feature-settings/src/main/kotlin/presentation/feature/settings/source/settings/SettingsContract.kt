package presentation.feature.settings.source.settings

import domain.core.source.model.AutoRebootModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.HomeAssistantInstanceModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.MqttModel
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.StreamingModel
import domain.core.source.model.ThemeModel
import domain.core.source.model.WebEngineModel
import domain.core.source.model.WebViewRefreshModel

/**
 * Represents the MVI state of the settings screen.
 *
 * This data class is used as the Orbit container state managed by [SettingsViewModel].
 *
 * @property theme The current visual theme selected by the user.
 * @property dock The current position of the control dock.
 * @property dashboardUrls The configured dashboard and whitelist URLs.
 * @property moveDetector The configuration for the motion detection service.
 * @property mqtt The configuration for the MQTT telemetry connection.
 * @property isLoading Whether initial data is still being loaded from repositories.
 * @property currentLanguage The current application language code (e.g., "en", "uk").
 * @see SettingsViewModel
 * @see SettingsScreen
 * @since 0.0.1
 */
public data class SettingsState(
    val theme: ThemeModel? = null,
    val dock: DockPositionModel? = null,
    val dashboardUrls: DashboardModel? = null,
    val moveDetector: MoveDetectorModel? = null,
    val mqtt: MqttModel? = null,
    val isLoading: Boolean = true,
    val currentLanguage: String? = null,
    val webEngine: WebEngineModel = WebEngineModel.AndroidWebView,
    val isAutoReturnEnabled: Boolean = true,
    val isDiscovering: Boolean = false,
    val webViewRefresh: WebViewRefreshModel? = null,
    val isReduceMotionEnabled: Boolean = false,
    val streaming: StreamingModel? = null,
    val screensaver: ScreensaverModel? = null,
    val autoReboot: AutoRebootModel? = null,
)

/**
 * One-off side effects that can occur on the settings screen.
 *
 * Side effects are consumed by [SettingsScreen] and trigger navigation,
 * system settings, or error display events.
 *
 * @see SettingsViewModel
 * @see SettingsScreen
 * @since 0.0.1
 */
public sealed class SettingsSideEffect {
    /** Navigates back to the previous screen. */
    public data object GoBackEffect : SettingsSideEffect()

    /** Navigates to the About/More information screen. */
    public data object GoMoreEffect : SettingsSideEffect()

    /** Navigates to the application selection screen. */
    public data object GoApplicationEffect : SettingsSideEffect()

    /** Attempts to open the system language settings. */
    public data object OpenLangSettingsEffect : SettingsSideEffect()

    /** Displays the in-app language selection dialog. */
    public data object ShowInAppLanguageSwitcherEffect : SettingsSideEffect()

    /** Triggers a complete restart of the application process. */
    public data object RestartApplicationEffect : SettingsSideEffect()

    /** Prompts the user to set Kite as the default home launcher. */
    public data object RequestDefaultLauncherEffect : SettingsSideEffect()

    /** Displays an error message to the user. */
    public data class ShowError(val message: String) : SettingsSideEffect()

    /**
     * Triggers the SAF "Create Document" picker to save the config JSON.
     *
     * @param json The serialized configuration to write into the file.
     */
    public data class ExportConfigEffect(val json: String) : SettingsSideEffect()

    /** Triggers the SAF "Open Document" picker to select a config JSON file for import. */
    public data object ImportConfigEffect : SettingsSideEffect()

    /** Triggers the SAF tree picker to select an image folder for the screensaver slideshow. */
    public data object PickScreensaverFolderEffect : SettingsSideEffect()

    /**
     * Delivers the list of discovered Home Assistant instances to display in a selection dialog.
     *
     * @param instances Discovered instances. May be empty if nothing was found.
     */
    public data class ShowDiscoveryResultEffect(
        val instances: List<HomeAssistantInstanceModel>,
    ) : SettingsSideEffect()
}

/**
 * User intents (actions) that can be performed on the settings screen.
 *
 * Intents are dispatched from [SettingsContent] and processed by
 * [SettingsViewModel.handleIntent].
 *
 * @see SettingsViewModel
 * @see SettingsContent
 * @since 0.0.1
 */
public sealed class SettingsIntent {
    /** Sets the application's visual theme. */
    public data class OnSetThemeIntent(val theme: ThemeModel) : SettingsIntent()

    /** Sets the kiosk dashboard and whitelist URLs. */
    public data class OnSetDashboardIntent(
        val dashboardUrl: String,
        val whitelistUrl: String,
        val trustAllSsl: Boolean = false,
    ) : SettingsIntent()

    /** Sets the control dock position. */
    public data class OnSetDockIntent(val dock: DockPositionModel) : SettingsIntent()

    /** Updates the motion detector configuration. */
    public data class OnSetMoveDetectorIntent(val moveDetector: MoveDetectorModel) : SettingsIntent()

    /** Updates the MQTT connection configuration. */
    public data class OnSetMqttIntent(val mqtt: MqttModel) : SettingsIntent()

    /** Request to navigate back. */
    public data object OnBackIntent : SettingsIntent()

    /** Request to change language. */
    public data object OnLangIntent : SettingsIntent()

    /** Result of attempting to open system language settings. */
    public data class OnLangSystemSettingsResultIntent(val wasOpened: Boolean) : SettingsIntent()

    /** Request to see more information. */
    public data object OnMoreIntent : SettingsIntent()

    /** Request to select allowed applications. */
    public data object OnApplicationIntent : SettingsIntent()

    /** Sets the application language directly. */
    public data class OnSetApplicationLanguageIntent(val localeCode: String) : SettingsIntent()

    /** Request to restart the application. */
    public data object OnRestartIntent : SettingsIntent()

    /** Sets the browser engine for the kiosk WebView. */
    public data class OnSetWebEngineIntent(val engine: WebEngineModel) : SettingsIntent()

    /** Request to set Kite as the default home launcher. */
    public data object OnSetDefaultLauncherIntent : SettingsIntent()

    /** Sets the auto-return to kiosk preference. */
    public data class OnSetAutoReturnIntent(val enabled: Boolean) : SettingsIntent()

    /** Request to export the current configuration to a JSON file. */
    public data object OnExportConfigIntent : SettingsIntent()

    /** Request to import configuration from a user-selected JSON file. */
    public data object OnImportConfigIntent : SettingsIntent()

    /**
     * Delivers the raw JSON content read from the import file.
     *
     * @param json The raw JSON string from the user-selected file.
     */
    public data class OnImportConfigContentIntent(val json: String) : SettingsIntent()

    /** Updates the periodic WebView refresh configuration. */
    public data class OnSetWebViewRefreshIntent(val refresh: WebViewRefreshModel) : SettingsIntent()

    /** Sets the reduce motion / disable animations preference. */
    public data class OnSetReduceMotionIntent(val enabled: Boolean) : SettingsIntent()

    /** Updates the camera streaming configuration. */
    public data class OnSetStreamingIntent(val streaming: StreamingModel) : SettingsIntent()

    /** Updates the screensaver configuration. */
    public data class OnSetScreensaverIntent(val screensaver: ScreensaverModel) : SettingsIntent()

    /** Updates the screensaver folder URI and switches the source to LOCAL_FOLDER. */
    public data class OnSetScreensaverFolderIntent(val uri: String) : SettingsIntent()

    /** Request to open the SAF tree picker for screensaver image folder selection. */
    public data object OnPickScreensaverFolderIntent : SettingsIntent()

    /** Updates the auto reboot schedule configuration. */
    public data class OnSetAutoRebootIntent(val model: AutoRebootModel) : SettingsIntent()

    /** Request to scan the local network for Home Assistant instances. */
    public data object OnDiscoverHomeAssistantIntent : SettingsIntent()

    /**
     * Populates the dashboard URL with the URL selected from the discovery result dialog.
     *
     * @param url The base URL of the selected Home Assistant instance.
     */
    public data class OnSelectDiscoveredInstanceIntent(val url: String) : SettingsIntent()
}
