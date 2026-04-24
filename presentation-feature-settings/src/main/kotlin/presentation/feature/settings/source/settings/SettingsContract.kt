package presentation.feature.settings.source.settings

import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.MqttModel
import domain.core.source.model.ThemeModel

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

    /** Displays an error message to the user. */
    public data class ShowError(val message: String) : SettingsSideEffect()
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
    public data class OnSetDashboardIntent(val dashboardUrl: String, val whitelistUrl: String) : SettingsIntent()

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
}
