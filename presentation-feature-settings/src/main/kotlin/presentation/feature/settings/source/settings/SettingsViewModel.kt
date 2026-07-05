package presentation.feature.settings.source.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.core.core.execute.executeResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import domain.core.source.monad.Failure
import domain.core.source.model.AutoRebootModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.MqttModel
import domain.core.source.model.ThemeModel
import domain.core.source.model.WebEngineModel
import domain.usecase.api.source.usecase.configuration.DiscoverHomeAssistantUseCase
import domain.usecase.api.source.usecase.configuration.GetAutoRebootUseCase
import domain.usecase.api.source.usecase.configuration.GetAutoReturnUseCase
import domain.usecase.api.source.usecase.configuration.GetApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.GetReduceMotionUseCase
import domain.usecase.api.source.usecase.configuration.GetWebViewRefreshUseCase
import domain.usecase.api.source.usecase.configuration.SetAutoRebootUseCase
import domain.usecase.api.source.usecase.configuration.SetReduceMotionUseCase
import domain.usecase.api.source.usecase.configuration.SetWebViewRefreshUseCase
import domain.core.source.model.WebViewRefreshModel
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.GetThemeUseCase
import domain.usecase.api.source.usecase.configuration.GetWebEngineUseCase
import domain.usecase.api.source.usecase.configuration.SetApplicationLanguageUseCase
import domain.usecase.api.source.usecase.configuration.SetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.SetThemeUseCase
import domain.usecase.api.source.usecase.configuration.SetAutoReturnUseCase
import domain.usecase.api.source.usecase.configuration.SetWebEngineUseCase
import domain.usecase.api.source.usecase.device.GetDockPositionUseCase
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.api.source.usecase.device.SetDockPositionUseCase
import domain.usecase.api.source.usecase.device.SetMoveDetectorUseCase
import domain.usecase.api.source.usecase.mqtt.GetMqttConfigurationUseCase
import domain.usecase.api.source.usecase.mqtt.SetMqttConfigurationUseCase
import domain.usecase.api.source.usecase.streaming.GetStreamingConfigurationUseCase
import domain.usecase.api.source.usecase.streaming.SetStreamingConfigurationUseCase
import domain.core.source.model.StreamingModel
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.ScreensaverSource
import domain.usecase.api.source.usecase.screensaver.GetScreensaverUseCase
import domain.usecase.api.source.usecase.screensaver.SetScreensaverUseCase
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
 *
 * @param getThemeUseCase Use case to retrieve the current visual theme.
 * @param setThemeUseCase Use case to persist the selected theme.
 * @param setDashboardUseCase Use case to persist dashboard and whitelist URLs.
 * @param getDashboardUseCase Use case to retrieve configured dashboard URLs.
 * @param getDockPositionUseCase Use case to retrieve the dock position preference.
 * @param setDockPositionUseCase Use case to persist the dock position preference.
 * @param getMoveDetectorUseCase Use case to retrieve motion detector configuration.
 * @param setMoveDetectorUseCase Use case to persist motion detector configuration.
 * @param getMqttConfigurationUseCase Use case to retrieve MQTT broker configuration.
 * @param setMqttConfigurationUseCase Use case to persist MQTT broker configuration.
 * @param setApplicationLanguageUseCase Use case to persist the application language preference.
 * @param getApplicationLanguageUseCase Use case to retrieve the application language preference.
 * @param getWebEngineUseCase Use case to retrieve the selected browser engine.
 * @param setWebEngineUseCase Use case to persist the selected browser engine.
 * @see SettingsScreen
 * @see SettingsState
 * @see SettingsSideEffect
 * @see SettingsIntent
 * @since 0.0.1
 */
@KoinViewModel
// Suppressed: MagicNumber for debounce delays (500ms); MaxLineLength for regex patterns.
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
    private val getWebEngineUseCase: GetWebEngineUseCase,
    private val setWebEngineUseCase: SetWebEngineUseCase,
    private val getAutoReturnUseCase: GetAutoReturnUseCase,
    private val setAutoReturnUseCase: SetAutoReturnUseCase,
    private val discoverHomeAssistantUseCase: DiscoverHomeAssistantUseCase,
    private val getWebViewRefreshUseCase: GetWebViewRefreshUseCase,
    private val setWebViewRefreshUseCase: SetWebViewRefreshUseCase,
    private val getReduceMotionUseCase: GetReduceMotionUseCase,
    private val setReduceMotionUseCase: SetReduceMotionUseCase,
    private val getStreamingConfigurationUseCase: GetStreamingConfigurationUseCase,
    private val setStreamingConfigurationUseCase: SetStreamingConfigurationUseCase,
    private val getScreensaverUseCase: GetScreensaverUseCase,
    private val setScreensaverUseCase: SetScreensaverUseCase,
    private val getAutoRebootUseCase: GetAutoRebootUseCase,
    private val setAutoRebootUseCase: SetAutoRebootUseCase,
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
     *
     * @param mqtt The [MqttModel] to validate.
     * @param dashboard The [DashboardModel] whose URL is a prerequisite for MQTT.
     * @return `true` if all mandatory fields pass their respective regex validations.
     * @since 0.0.1
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

    private var streamingJob: Job? = null

    init {
        loadTheme()
        loadDashboardUrls()
        loadDockPosition()
        loadMoveDetector()
        loadMqtt()
        loadApplicationLanguage()
        loadWebEngine()
        loadAutoReturn()
        loadWebViewRefresh()
        loadReduceMotion()
        loadStreaming()
        loadScreensaver()
        loadAutoReboot()
    }

    /**
     * Loads the current application language from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @param theme The [ThemeModel] to apply and persist.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @param dock The [DockPositionModel] to apply and persist.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @param dashboardUrl The Home Assistant dashboard URL.
     * @param whitelistUrl The comma-separated whitelist domain(s) for WebView navigation.
     * @param trustAllSsl Whether to bypass SSL certificate validation for self-signed certs.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
     */
    public fun onSetDashboard(dashboardUrl: String, whitelistUrl: String, trustAllSsl: Boolean = false): Job = executeResult(
        scope = viewModelScope,
        request = {
            setDashboardUseCase(DashboardModel(dashboardUrl.trim(), whitelistUrl.trim(), trustAllSsl))
        },
        result = {
            intent {
                val dashboard = DashboardModel(dashboardUrl.trim(), whitelistUrl.trim(), trustAllSsl)
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
     *
     * @param moveDetector The [MoveDetectorModel] configuration to apply and persist.
     * @since 0.0.1
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
     *
     * @param mqtt The [MqttModel] configuration to validate, apply, and persist.
     * @since 0.0.1
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
     *
     * @param localeCode The IETF BCP 47 language tag (e.g., "en", "uk").
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
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
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onLang(): Job = intent {
        postSideEffect(SettingsSideEffect.OpenLangSettingsEffect)
    }

    /**
     * Handles the result of opening system language settings.
     * Shows in-app switcher if system settings were not opened.
     *
     * @param wasSystemSettingsOpened Whether the system language settings were successfully opened.
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onLangResult(wasSystemSettingsOpened: Boolean): Job = intent {
        if (!wasSystemSettingsOpened) {
            postSideEffect(SettingsSideEffect.ShowInAppLanguageSwitcherEffect)
        }
    }

    /**
     * Triggers the side effect to navigate to the About screen.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onMore(): Job = intent {
        postSideEffect(SettingsSideEffect.GoMoreEffect)
    }

    /**
     * Triggers the side effect to navigate back.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onBack(): Job = intent {
        postSideEffect(SettingsSideEffect.GoBackEffect)
    }

    /**
     * Triggers the side effect to navigate to the application selection screen.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onApplication(): Job = intent {
        postSideEffect(SettingsSideEffect.GoApplicationEffect)
    }

    /**
     * Triggers the side effect to restart the application.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onRestart(): Job = intent {
        postSideEffect(SettingsSideEffect.RestartApplicationEffect)
    }

    /**
     * Loads the current browser engine selection from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.4
     */
    private fun loadWebEngine(): Job = executeResult(
        scope = viewModelScope,
        request = { getWebEngineUseCase() },
        result = { engine ->
            intent {
                reduce { state.copy(webEngine = engine ?: WebEngineModel.AndroidWebView) }
            }
        },
        errorBlock = {
            intent {
                reduce { state.copy(webEngine = WebEngineModel.AndroidWebView) }
            }
        },
    )

    /**
     * Updates and persists the browser engine selection.
     *
     * @param engine The [WebEngineModel] to apply and persist.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.4
     */
    public fun onSetWebEngine(engine: WebEngineModel): Job = executeResult(
        scope = viewModelScope,
        request = { setWebEngineUseCase(engine) },
        result = {
            intent {
                reduce { state.copy(webEngine = engine) }
            }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Loads the auto-return-to-kiosk preference from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.4
     */
    private fun loadAutoReturn(): Job = executeResult(
        scope = viewModelScope,
        request = { getAutoReturnUseCase() },
        result = { enabled ->
            intent { reduce { state.copy(isAutoReturnEnabled = enabled ?: true) } }
        },
        errorBlock = {
            intent { reduce { state.copy(isAutoReturnEnabled = true) } }
        },
    )

    /**
     * Updates and persists the auto-return-to-kiosk preference.
     *
     * @param enabled Whether the device should automatically return to the kiosk after
     *   [HostActivity][presentation.feature.host.source.host.HostActivity] is backgrounded.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.4
     */
    public fun onSetAutoReturn(enabled: Boolean): Job = executeResult(
        scope = viewModelScope,
        request = { setAutoReturnUseCase(enabled) },
        result = {
            intent { reduce { state.copy(isAutoReturnEnabled = enabled) } }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Loads the periodic WebView refresh configuration from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    private fun loadWebViewRefresh(): Job = executeResult(
        scope = viewModelScope,
        request = { getWebViewRefreshUseCase() },
        result = { refresh ->
            intent { reduce { state.copy(webViewRefresh = refresh) } }
        },
        errorBlock = {
            intent { reduce { state.copy(webViewRefresh = WebViewRefreshModel(enabled = false, intervalSeconds = 300L)) } }
        },
    )

    /**
     * Updates and persists the periodic WebView refresh configuration.
     *
     * Automatically disables refresh if [WebViewRefreshModel.intervalSeconds] is zero or null.
     * Debounced by 500 ms to prevent excessive writes during slider interaction.
     *
     * @param refresh The [WebViewRefreshModel] to validate, apply, and persist.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    public fun onSetWebViewRefresh(refresh: WebViewRefreshModel): Job {
        val validated = if ((refresh.intervalSeconds ?: 0L) == 0L) {
            refresh.copy(enabled = false)
        } else {
            refresh
        }
        intent { reduce { state.copy(webViewRefresh = validated) } }
        return executeResult(
            scope = viewModelScope,
            request = {
                delay(500)
                setWebViewRefreshUseCase(validated)
            },
            result = { /* Persistence success */ },
            errorBlock = { handleError(it) },
        )
    }

    /**
     * Loads the reduce-motion / disable-animations preference from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    private fun loadReduceMotion(): Job = executeResult(
        scope = viewModelScope,
        request = { getReduceMotionUseCase() },
        result = { enabled ->
            intent { reduce { state.copy(isReduceMotionEnabled = enabled ?: false) } }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Updates and persists the reduce-motion / disable-animations preference.
     *
     * When `true`, the [ValueAnimator][android.animation.ValueAnimator] duration scale is set to
     * 0 in [presentation.feature.host.source.host.HostActivity], suppressing all system animations.
     *
     * @param enabled Whether to reduce motion globally.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    public fun onSetReduceMotion(enabled: Boolean): Job = executeResult(
        scope = viewModelScope,
        request = { setReduceMotionUseCase(enabled) },
        result = {
            intent { reduce { state.copy(isReduceMotionEnabled = enabled) } }
        },
        errorBlock = { handleError(it) },
    )

    /**
     * Loads the MJPEG camera streaming configuration from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.1.0
     */
    private fun loadStreaming(): Job = executeResult(
        scope = viewModelScope,
        request = { getStreamingConfigurationUseCase() },
        result = { streaming ->
            intent { reduce { state.copy(streaming = streaming) } }
        },
        errorBlock = {
            intent { reduce { state.copy(streaming = StreamingModel()) } }
        },
    )

    /**
     * Updates and persists the MJPEG camera streaming configuration.
     *
     * Debounced by 1 000 ms to avoid flooding [presentation.core.platform.source.streaming.MjpegHttpServer]
     * with rapid config changes while the user adjusts quality or FPS sliders.
     *
     * @param streaming The [StreamingModel] to apply and persist.
     * @return The [Job] associated with the use case execution.
     * @since 0.1.0
     */
    public fun onSetStreaming(streaming: StreamingModel): Job {
        streamingJob?.cancel()
        intent { reduce { state.copy(streaming = streaming) } }
        streamingJob = executeResult(
            scope = viewModelScope,
            request = {
                delay(1000)
                setStreamingConfigurationUseCase(streaming)
            },
            result = { /* Persistence success */ },
            errorBlock = { handleError(it) },
        )
        return streamingJob!!
    }

    /**
     * Loads the screensaver configuration from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.1.0
     */
    private fun loadScreensaver(): Job = executeResult(
        scope = viewModelScope,
        request = { getScreensaverUseCase() },
        result = { screensaver ->
            intent { reduce { state.copy(screensaver = screensaver) } }
        },
        errorBlock = {
            // NOTE: Default screensaver is disabled with sensible fallbacks so the UI renders
            // correctly even when the DataStore is empty on first launch.
            intent { reduce { state.copy(screensaver = ScreensaverModel(
                enabled = false,
                activationDelay = 60L,
                slideInterval = 30L,
                showClock = true,
                source = ScreensaverSource.BLACK,
                localFolderUri = null,
            )) } }
        },
    )

    /**
     * Updates and persists the screensaver configuration.
     *
     * Debounced by 500 ms to avoid redundant writes while the user adjusts slider values.
     *
     * @param screensaver The [ScreensaverModel] to apply and persist.
     * @return The [Job] associated with the use case execution.
     * @since 0.1.0
     */
    public fun onSetScreensaver(screensaver: ScreensaverModel): Job {
        intent { reduce { state.copy(screensaver = screensaver) } }
        return executeResult(
            scope = viewModelScope,
            request = {
                delay(500)
                setScreensaverUseCase(screensaver)
            },
            result = { /* Persistence success */ },
            errorBlock = { handleError(it) },
        )
    }

    /**
     * Loads the auto-reboot schedule configuration from persistence.
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    private fun loadAutoReboot(): Job = executeResult(
        scope = viewModelScope,
        request = { getAutoRebootUseCase() },
        result = { autoReboot ->
            intent { reduce { state.copy(autoReboot = autoReboot) } }
        },
        errorBlock = {
            intent { reduce { state.copy(autoReboot = AutoRebootModel()) } }
        },
    )

    /**
     * Updates and persists the auto-reboot schedule configuration.
     *
     * Changes are applied immediately (no debounce) because rescheduling an alarm is idempotent
     * and the user explicitly taps a control rather than dragging a slider.
     *
     * @param model The [AutoRebootModel] to apply and persist.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    public fun onSetAutoReboot(model: AutoRebootModel): Job {
        intent { reduce { state.copy(autoReboot = model) } }
        return executeResult(
            scope = viewModelScope,
            request = {
                setAutoRebootUseCase(model)
            },
            result = { /* Persistence success */ },
            errorBlock = { handleError(it) },
        )
    }

    /**
     * Handles incoming user intents and routes them to the appropriate logic.
     *
     * @param intent The user action to process.
     * @see SettingsIntent
     * @since 0.0.1
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
                    intent.trustAllSsl,
                )

            SettingsIntent.OnMoreIntent -> onMore()
            SettingsIntent.OnApplicationIntent -> onApplication()
            SettingsIntent.OnRestartIntent -> onRestart()
            is SettingsIntent.OnSetDockIntent -> onSetDockPosition(intent.dock)
            is SettingsIntent.OnSetMoveDetectorIntent -> onSetMoveDetector(intent.moveDetector)
            is SettingsIntent.OnSetMqttIntent -> onSetMqtt(intent.mqtt)
            is SettingsIntent.OnSetApplicationLanguageIntent -> onSetApplicationLanguage(intent.localeCode)
            is SettingsIntent.OnSetWebEngineIntent -> onSetWebEngine(intent.engine)
            SettingsIntent.OnSetDefaultLauncherIntent -> onSetDefaultLauncher()
            is SettingsIntent.OnSetAutoReturnIntent -> onSetAutoReturn(intent.enabled)
            SettingsIntent.OnExportConfigIntent -> onExportConfig()
            SettingsIntent.OnImportConfigIntent -> onImportConfig()
            is SettingsIntent.OnImportConfigContentIntent -> onImportConfigContent(intent.json)
            SettingsIntent.OnDiscoverHomeAssistantIntent -> onDiscoverHomeAssistant()
            is SettingsIntent.OnSelectDiscoveredInstanceIntent -> onSelectDiscoveredInstance(intent.url)
            is SettingsIntent.OnSetWebViewRefreshIntent -> onSetWebViewRefresh(intent.refresh)
            is SettingsIntent.OnSetReduceMotionIntent -> onSetReduceMotion(intent.enabled)
            is SettingsIntent.OnSetStreamingIntent -> onSetStreaming(intent.streaming)
            is SettingsIntent.OnSetScreensaverIntent -> onSetScreensaver(intent.screensaver)
            is SettingsIntent.OnSetAutoRebootIntent -> onSetAutoReboot(intent.model)
            is SettingsIntent.OnSetScreensaverFolderIntent -> {
                val current = container.stateFlow.value.screensaver ?: ScreensaverModel(
                    enabled = false,
                    activationDelay = 60L,
                    slideInterval = 30L,
                    showClock = true,
                    source = ScreensaverSource.BLACK,
                    localFolderUri = null,
                )
                onSetScreensaver(current.copy(localFolderUri = intent.uri, source = ScreensaverSource.LOCAL_FOLDER))
            }
            SettingsIntent.OnPickScreensaverFolderIntent -> intent {
                postSideEffect(SettingsSideEffect.PickScreensaverFolderEffect)
            }
        }
    }

    /**
     * Triggers the side effect to prompt the user to set Kite as the default launcher.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.4
     */
    public fun onSetDefaultLauncher(): Job = intent {
        postSideEffect(SettingsSideEffect.RequestDefaultLauncherEffect)
    }

    /**
     * Serializes the current settings state to JSON and posts [SettingsSideEffect.ExportConfigEffect].
     *
     * Sensitive fields (MQTT password) are excluded from the export.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.5
     */
    public fun onExportConfig(): Job = intent {
        val snapshot = ConfigSnapshot(
            dashboardUrl = state.dashboardUrls?.dashboardUrl ?: "",
            whitelistUrl = state.dashboardUrls?.whitelistUrl ?: "",
            trustAllSsl = state.dashboardUrls?.trustAllSsl ?: false,
            theme = state.theme?.name ?: "Light",
            dockPosition = state.dock?.position?.name ?: "Left",
            isAutoReturnEnabled = state.isAutoReturnEnabled,
            webEngine = state.webEngine.name,
            mqttIp = state.mqtt?.ip ?: "",
            mqttPort = state.mqtt?.port ?: "",
            mqttClientId = state.mqtt?.clientId ?: "",
            mqttUsername = state.mqtt?.username ?: "",
            mqttFriendlyName = state.mqtt?.friendlyName ?: "",
            isMqttEnabled = state.mqtt?.enabled ?: false,
            motionSensitivity = state.moveDetector?.sensitivity ?: 50,
            motionDimDelay = state.moveDetector?.dimDelay ?: 30L,
            motionScreenTimeout = state.moveDetector?.screenTimeout ?: 60L,
            motionFabDelay = state.moveDetector?.fabDelay ?: 60L,
            isMotionEnabled = state.moveDetector?.enabled ?: false,
        )
        val json = configJson.encodeToString(snapshot)
        postSideEffect(SettingsSideEffect.ExportConfigEffect(json))
    }

    /**
     * Triggers the SAF file picker for importing a configuration file.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.5
     */
    public fun onImportConfig(): Job = intent {
        postSideEffect(SettingsSideEffect.ImportConfigEffect)
    }

    /**
     * Parses [json] and applies all recognised fields back to the DataStore preferences.
     *
     * Unknown or missing keys are silently ignored.
     *
     * @param json Raw JSON string from the imported file.
     * @return The [Job] associated with the use case executions.
     * @since 0.0.5
     */
    public fun onImportConfigContent(json: String): Job {
        // Capture mutable state before the suspend lambda to avoid 'state' being unresolvable
        // inside the CoroutineScope receiver context of executeResult's request block.
        val currentMqtt = container.stateFlow.value.mqtt ?: MqttModel()
        return executeResult(
        scope = viewModelScope,
        request = {
            val snapshot = configJson.decodeFromString<ConfigSnapshot>(json)

            val theme = ThemeModel.entries.find { it.name == snapshot.theme } ?: ThemeModel.Light
            setThemeUseCase(theme)

            val dockPos = DockPositionModel.Position.entries.find { it.name == snapshot.dockPosition }
                ?: DockPositionModel.Position.Left
            setDockPositionUseCase(DockPositionModel(dockPos))

            setDashboardUseCase(DashboardModel(snapshot.dashboardUrl, snapshot.whitelistUrl, snapshot.trustAllSsl))

            setAutoReturnUseCase(snapshot.isAutoReturnEnabled)

            val engine = WebEngineModel.entries.find { it.name == snapshot.webEngine }
                ?: WebEngineModel.AndroidWebView
            setWebEngineUseCase(engine)

            setMqttConfigurationUseCase(
                currentMqtt.copy(
                    ip = snapshot.mqttIp,
                    port = snapshot.mqttPort,
                    clientId = snapshot.mqttClientId,
                    username = snapshot.mqttUsername,
                    friendlyName = snapshot.mqttFriendlyName,
                    enabled = snapshot.isMqttEnabled,
                ),
            )

            setMoveDetectorUseCase(
                MoveDetectorModel(
                    enabled = snapshot.isMotionEnabled,
                    sensitivity = snapshot.motionSensitivity,
                    dimDelay = snapshot.motionDimDelay,
                    screenTimeout = snapshot.motionScreenTimeout,
                    fabDelay = snapshot.motionFabDelay,
                ),
            )

            Result.success(Unit)
        },
        result = {
            // Reload all state from persistence to reflect the imported values.
            loadTheme()
            loadDashboardUrls()
            loadDockPosition()
            loadMoveDetector()
            loadMqtt()
            loadWebEngine()
            loadAutoReturn()
        },
        errorBlock = { handleError(it) },
        )
    }

    /**
     * Scans the local network for Home Assistant instances and posts a [SettingsSideEffect.ShowDiscoveryResultEffect].
     *
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    public fun onDiscoverHomeAssistant(): Job = executeResult(
        scope = viewModelScope,
        request = {
            intent { reduce { state.copy(isDiscovering = true) } }
            discoverHomeAssistantUseCase()
        },
        result = { instances ->
            intent {
                reduce { state.copy(isDiscovering = false) }
                postSideEffect(SettingsSideEffect.ShowDiscoveryResultEffect(instances ?: emptyList()))
            }
        },
        errorBlock = { failure ->
            intent { reduce { state.copy(isDiscovering = false) } }
            handleError(failure)
        },
    )

    /**
     * Applies a URL selected from the discovery result dialog to the dashboard URL field.
     *
     * The existing whitelist URL is preserved.
     *
     * @param url The base URL of the selected Home Assistant instance.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.5
     */
    public fun onSelectDiscoveredInstance(url: String): Job {
        val whitelist = container.stateFlow.value.dashboardUrls?.whitelistUrl ?: ""
        val currentTrustAllSsl = container.stateFlow.value.dashboardUrls?.trustAllSsl ?: false
        return executeResult(
            scope = viewModelScope,
            request = { setDashboardUseCase(DashboardModel(url, whitelist, currentTrustAllSsl)) },
            result = {
                intent {
                    reduce {
                        state.copy(
                            dashboardUrls = state.dashboardUrls?.copy(dashboardUrl = url)
                                ?: DashboardModel(url, ""),
                        )
                    }
                }
            },
            errorBlock = { handleError(it) },
        )
    }

    private val configJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    /**
     * Internal JSON snapshot model for config import/export.
     *
     * Sensitive fields (MQTT password) are intentionally excluded.
     */
    @Serializable
    private data class ConfigSnapshot(
        val dashboardUrl: String = "",
        val whitelistUrl: String = "",
        val trustAllSsl: Boolean = false,
        val theme: String = "Light",
        val dockPosition: String = "Left",
        val isAutoReturnEnabled: Boolean = true,
        val webEngine: String = "AndroidWebView",
        val mqttIp: String = "",
        val mqttPort: String = "",
        val mqttClientId: String = "",
        val mqttUsername: String = "",
        val mqttFriendlyName: String = "",
        val isMqttEnabled: Boolean = false,
        val motionSensitivity: Int = 50,
        val motionDimDelay: Long = 30L,
        val motionScreenTimeout: Long = 60L,
        val motionFabDelay: Long = 60L,
        val isMotionEnabled: Boolean = false,
    )

}
