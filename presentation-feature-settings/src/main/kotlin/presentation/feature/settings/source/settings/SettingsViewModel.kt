package presentation.feature.settings.source.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.core.core.execute.executeResult
import domain.core.core.monad.Failure
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.MqttModel
import domain.core.source.model.ThemeModel
import domain.usecase.api.source.usecase.configuration.GetApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.GetThemeUseCase
import domain.usecase.api.source.usecase.configuration.SetApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.SetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.SetThemeUseCase
import domain.usecase.api.source.usecase.device.GetDockPositionUseCase
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.api.source.usecase.device.SetDockPositionUseCase
import domain.usecase.api.source.usecase.device.SetMoveDetectorUseCase
import domain.usecase.api.source.usecase.mqtt.GetMqttConfigurationUseCase
import domain.usecase.api.source.usecase.mqtt.SetMqttConfigurationUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import presentation.core.localisation.R

/**
 * ViewModel for the settings screen, managing all user configurations.
 *
 * This component orchestrates the loading and saving of various settings, including
 * theme, dock position, kiosk URLs, motion detector parameters, and MQTT connectivity.
 * It uses the Orbit MVI framework to manage state and side effects.
 */
@KoinViewModel
@Suppress("MagicNumber", "MaxLineLength")
public class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setDashboardUseCase: SetDashboardUseCase,
    private val getDashboardUseCase: GetDashboardUseCase,
    private val getDockPositionUseCase: GetDockPositionUseCase,
    private val setDockPositionUseCase: SetDockPositionUseCase,
    private val getMoveDetectorUseCase: GetMoveDetectorUseCase,
    private val setMoveDetectorUseCase: SetMoveDetectorUseCase,
    private val getMqttConfigurationUseCase: GetMqttConfigurationUseCase,
    private val setMqttConfigurationUseCase: SetMqttConfigurationUseCase,
    private val setApplicationLanguageUseCase: SetApplicationLanguageUseCase,
    private val getApplicationLanguageUseCase: GetApplicationLanguageUseCase,
) : ContainerHost<SettingsState, SettingsSideEffect>,
    ViewModel() {
    public override val container: Container<SettingsState, SettingsSideEffect> =
        container(SettingsState())

    private fun mapError(failure: Throwable): Int = when (failure) {
        is Failure.Technical.Network -> R.string.error_network
        is Failure.Technical.Database -> R.string.error_database
        is Failure.Technical.Preference -> R.string.error_preference
        is Failure.Logic.NotFound -> R.string.error_not_found
        is Failure.Logic.Business -> R.string.error_unknown
        else -> R.string.error_unknown
    }

    private fun handleError(failure: Throwable) {
        if (failure is CancellationException) return
        intent {
            postSideEffect(SettingsSideEffect.ShowError(mapError(failure).toString()))
        }
    }

    private var moveDetectorJob: Job? = null
    private var mqttJob: Job? = null

    // Regex patterns for validation
    private val ipRegex =
        Regex(
            "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$|^(?!:\\/\\/)([a-zA-Z0-9-_]+\\.?)*[a-zA-Z0-9][a-zA-Z0-9-_]*$",
        )
    private val portRegex =
        Regex("^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")
    private val commonRegex = Regex("^[a-zA-Z0-9_\\-\\.\\s]{1,64}$")
    private val dashboardRegex =
        Regex("^(https?://)?([\\da-z\\.-]+)(:[0-9]{1,5})?([/\\w \\.-]*)*\\/?$")
    private val whitelistRegex = Regex("^([a-zA-Z0-9_\\-\\.\\s,]*)$")

    /**
     * Validates the complete MQTT configuration including dependent Dashboard fields.
     * Mandatory: IP, Port, ClientID, Username, Password, FriendlyName, DashboardURL.
     * Optional: Whitelist (must be valid if not empty).
     */
    private fun isMqttConfigurationValid(mqtt: MqttModel, dashboard: DashboardModel?): Boolean {
        val isIpValid = mqtt.ip?.let { ipRegex.matches(it) } ?: false
        val isPortValid = mqtt.port?.let { portRegex.matches(it) } ?: false
        val isClientIdValid = mqtt.clientId?.let { commonRegex.matches(it) } ?: false
        val isUsernameValid = mqtt.username?.let { commonRegex.matches(it) } ?: false
        val isPasswordValid = !mqtt.password.isNullOrEmpty()
        val isFriendlyNameValid = mqtt.friendlyName?.let { commonRegex.matches(it) } ?: false

        val isDashboardUrlValid =
            dashboard?.dashboardUrl?.let { dashboardRegex.matches(it) } ?: false
        val isWhitelistValid =
            dashboard?.whitelistUrl?.let { it.isEmpty() || whitelistRegex.matches(it) } ?: true

        return isIpValid && isPortValid && isClientIdValid && isUsernameValid &&
            isPasswordValid && isFriendlyNameValid && isDashboardUrlValid && isWhitelistValid
    }

    init {
        loadTheme()
        loadDashboardUrls()
        loadDockPosition()
        loadMoveDetector()
        loadMqtt()
        loadApplicationLanguage()
    }

    /**
     * Loads the current application language from persistence.
     */
    private fun loadApplicationLanguage(): Job = executeResult(
        scope = viewModelScope,
        request = { getApplicationLanguageUseCase() },
        result = { localeCode ->
            intent {
                reduce { state.copy(currentLanguage = localeCode) }
            }
        },
        errorBlock = {
            intent {
                reduce { state.copy(currentLanguage = null) }
            }
        },
    )

    /**
     * Loads the current theme configuration.
     */
    private fun loadTheme(): Job = executeResult(
        scope = viewModelScope,
        request = { getThemeUseCase() },
        result = { theme ->
            intent {
                reduce { state.copy(theme = theme ?: ThemeModel.Light) }
            }
        },
    )

    /**
     * Loads the control dock position configuration.
     */
    private fun loadDockPosition(): Job = executeResult(
        scope = viewModelScope,
        request = { getDockPositionUseCase() },
        result = { dock ->
            intent {
                reduce { state.copy(dock = dock) }
            }
        },
        errorBlock = {
            intent {
                reduce { state.copy(dock = DockPositionModel(DockPositionModel.Position.Left)) }
            }
        },
    )

    /**
     * Loads the configured dashboard and whitelist URLs.
     */
    private fun loadDashboardUrls(): Job = executeResult(
        scope = viewModelScope,
        request = {
            getDashboardUseCase()
        },
        result = { dashboardModel ->
            intent {
                reduce {
                    state.copy(dashboardUrls = dashboardModel, isLoading = false)
                }
            }
        },
        errorBlock = {
            intent {
                reduce {
                    state.copy(dashboardUrls = null, isLoading = false)
                }
            }
        },
    )

    /**
     * Loads the motion detector configuration.
     */
    private fun loadMoveDetector(): Job = executeResult(
        scope = viewModelScope,
        request = { getMoveDetectorUseCase() },
        result = { moveDetector ->
            intent {
                reduce { state.copy(moveDetector = moveDetector) }
            }
        },
    )

    /**
     * Loads the MQTT telemetry configuration.
     */
    private fun loadMqtt(): Job = executeResult(
        scope = viewModelScope,
        request = { getMqttConfigurationUseCase() },
        result = { mqtt ->
            intent {
                reduce { state.copy(mqtt = mqtt) }
            }
        },
    )

    /**
     * Updates and persists the visual theme.
     */
    public fun onSetTheme(theme: ThemeModel): Job = executeResult(
        scope = viewModelScope,
        request = {
            setThemeUseCase(theme)
        },
        result = {
            intent {
                reduce { state.copy(theme = theme) }
            }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Updates and persists the dock position.
     */
    public fun onSetDockPosition(dock: DockPositionModel): Job = executeResult(
        scope = viewModelScope,
        request = { setDockPositionUseCase(dock) },
        result = {
            intent {
                reduce { state.copy(dock = dock) }
            }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Updates and persists the dashboard configuration.
     * Re-validates MQTT configuration based on the new dashboard URL.
     */
    public fun onSetDashboard(dashboardUrl: String, whitelistUrl: String): Job = executeResult(
        scope = viewModelScope,
        request = {
            setDashboardUseCase(DashboardModel(dashboardUrl.trim(), whitelistUrl.trim()))
        },
        result = {
            intent {
                val dashboard = DashboardModel(dashboardUrl.trim(), whitelistUrl.trim())
                val currentMqtt = state.mqtt ?: MqttModel()
                val isConfigValid = isMqttConfigurationValid(currentMqtt, dashboard)
                val validatedMqtt =
                    if (isConfigValid) currentMqtt else currentMqtt.copy(enabled = false)

                reduce { state.copy(dashboardUrls = dashboard, mqtt = validatedMqtt) }

                if (validatedMqtt != currentMqtt) {
                    mqttJob?.cancel()
                    mqttJob =
                        executeResult(
                            scope = viewModelScope,
                            request = { setMqttConfigurationUseCase(validatedMqtt) },
                            result = { /* Persistence success */ },
                            errorBlock = { handleError(it) },
                        )
                }
            }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Updates and persists the motion detector configuration.
     * Automatically disables the detector if any delay or sensitivity parameter is zero.
     */
    public fun onSetMoveDetector(moveDetector: MoveDetectorModel) {
        val isDisabledLogic =
            (moveDetector.sensitivity ?: 0) == 0 ||
                (moveDetector.dimDelay ?: 0L) == 0L ||
                (moveDetector.screenTimeout ?: 0L) == 0L ||
                (moveDetector.fabDelay ?: 0L) == 0L

        val validatedMoveDetector =
            if (isDisabledLogic) {
                moveDetector.copy(enabled = false)
            } else {
                moveDetector
            }

        intent {
            reduce { state.copy(moveDetector = validatedMoveDetector) }
        }

        moveDetectorJob?.cancel()
        moveDetectorJob =
            executeResult(
                scope = viewModelScope,
                request = {
                    delay(500) // Debounce for 500ms
                    setMoveDetectorUseCase(validatedMoveDetector)
                },
                result = {
                    // Persistence success
                },
                errorBlock = { handleError(it) },
            )
    }

    /**
     * Updates and persists the MQTT configuration with strict validation.
     * Automatically disables MQTT if any field (including Dashboard URL) is invalid.
     */
    public fun onSetMqtt(mqtt: MqttModel) {
        val trimmedMqtt =
            mqtt.copy(
                ip = mqtt.ip?.trim(),
                port = mqtt.port?.trim(),
                clientId = mqtt.clientId?.trim(),
                username = mqtt.username?.trim(),
                password = mqtt.password?.trim(),
                friendlyName = mqtt.friendlyName?.trim(),
            )

        intent {
            val dashboard = state.dashboardUrls
            val isValid = isMqttConfigurationValid(trimmedMqtt, dashboard)
            val validatedMqtt = if (isValid) trimmedMqtt else trimmedMqtt.copy(enabled = false)

            reduce { state.copy(mqtt = validatedMqtt) }

            mqttJob?.cancel()
            mqttJob =
                executeResult(
                    scope = viewModelScope,
                    request = {
                        delay(500) // Debounce for 500ms
                        setMqttConfigurationUseCase(validatedMqtt)
                    },
                    result = {
                        // Persistence success
                    },
                    errorBlock = { handleError(it) },
                )
        }
    }

    /**
     * Updates the application language and applies the locale change globally.
     */
    public fun onSetApplicationLanguage(localeCode: String): Job = executeResult(
        scope = viewModelScope,
        request = {
            setApplicationLanguageUseCase(localeCode)
        },
        result = {
            intent {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeCode))
                reduce { state.copy(currentLanguage = localeCode) }
            }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Triggers the side effect to open system language settings.
     */
    public fun onLang(): Job = intent {
        postSideEffect(SettingsSideEffect.OpenLangSettingsEffect)
    }

    /**
     * Handles the result of opening system language settings.
     * Shows in-app switcher if system settings were not opened.
     */
    public fun onLangResult(wasSystemSettingsOpened: Boolean): Job = intent {
        if (!wasSystemSettingsOpened) {
            postSideEffect(SettingsSideEffect.ShowInAppLanguageSwitcherEffect)
        }
    }

    /**
     * Triggers the side effect to navigate to the About screen.
     */
    public fun onMore(): Job = intent {
        postSideEffect(SettingsSideEffect.GoMoreEffect)
    }

    /**
     * Triggers the side effect to navigate back.
     */
    public fun onBack(): Job = intent {
        postSideEffect(SettingsSideEffect.GoBackEffect)
    }

    /**
     * Triggers the side effect to navigate to the application selection screen.
     */
    public fun onApplication(): Job = intent {
        postSideEffect(SettingsSideEffect.GoApplicationEffect)
    }

    /**
     * Triggers the side effect to restart the application.
     */
    public fun onRestart(): Job = intent {
        postSideEffect(SettingsSideEffect.RestartApplicationEffect)
    }

    /**
     * Handles incoming user intents and routes them to the appropriate logic.
     *
     * @param intent The user action to process.
     */
    public fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OnBackIntent -> onBack()
            is SettingsIntent.OnLangIntent -> onLang()
            is SettingsIntent.OnLangSystemSettingsResultIntent -> onLangResult(intent.wasOpened)
            is SettingsIntent.OnSetThemeIntent -> onSetTheme(intent.theme)
            is SettingsIntent.OnSetDashboardIntent ->
                onSetDashboard(
                    intent.dashboardUrl,
                    intent.whitelistUrl,
                )

            SettingsIntent.OnMoreIntent -> onMore()
            SettingsIntent.OnApplicationIntent -> onApplication()
            SettingsIntent.OnRestartIntent -> onRestart()
            is SettingsIntent.OnSetDockIntent -> onSetDockPosition(intent.dock)
            is SettingsIntent.OnSetMoveDetectorIntent -> onSetMoveDetector(intent.moveDetector)
            is SettingsIntent.OnSetMqttIntent -> onSetMqtt(intent.mqtt)
            is SettingsIntent.OnSetApplicationLanguageIntent -> onSetApplicationLanguage(intent.localeCode)
        }
    }
}
