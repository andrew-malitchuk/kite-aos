package presentation.feature.main.source.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.core.core.execute.executeResult
import domain.core.source.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.ScreenStateModel
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.ScreensaverSource
import domain.core.source.model.WebEngineModel
import domain.core.source.model.WebViewRefreshModel
import domain.usecase.api.source.usecase.application.LoadApplicationsUseCase
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.GetWebEngineUseCase
import domain.usecase.api.source.usecase.configuration.GetWebViewRefreshUseCase
import domain.usecase.api.source.usecase.configuration.ObserveNetworkStatusUseCase
import domain.usecase.api.source.usecase.device.GetDockPositionUseCase
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.api.source.usecase.device.ObserveMoveDetectorMotionUseCase
import domain.usecase.api.source.usecase.device.ObserveScreenStateUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendNetworkStateUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendUrlUseCase
import domain.usecase.api.source.usecase.device.EmitScreenStateUseCase
import domain.usecase.api.source.usecase.mqtt.ObserveMqttFabCommandUseCase
import domain.usecase.api.source.usecase.mqtt.ObserveMqttScreensaverCommandUseCase
import domain.usecase.api.source.usecase.screensaver.GetScreensaverUseCase
import domain.usecase.api.source.usecase.streaming.ObserveStreamingConfigurationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import presentation.core.localisation.R

/**
 * ViewModel for the Main (Kiosk) screen.
 *
 * It orchestrates the dashboard URL loading, manages the control drawer visibility,
 * and reacts to motion detection events to show/hide navigation controls.
 *
 * @param getDashboardUseCase Use case to retrieve the configured dashboard URLs.
 * @param loadApplicationsUseCase Use case to load chosen applications for the drawer.
 * @param getDockPositionUseCase Use case to retrieve the dock position preference.
 * @param getMoveDetectorUseCase Use case to retrieve the motion detector configuration.
 * @param observeMoveDetectorMotionUseCase Use case to observe real-time motion detection events.
 * @param mqttSendUrlUseCase Use case to publish the current WebView URL to the MQTT broker.
 * @param getWebEngineUseCase Use case to retrieve the selected browser engine.
 * @see MainScreen
 * @see MainState
 * @see MainSideEffect
 * @see MainIntent
 * @since 0.0.1
 */
@KoinViewModel
public class MainViewModel(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val loadApplicationsUseCase: LoadApplicationsUseCase,
    private val getDockPositionUseCase: GetDockPositionUseCase,
    private val getMoveDetectorUseCase: GetMoveDetectorUseCase,
    private val observeMoveDetectorMotionUseCase: ObserveMoveDetectorMotionUseCase,
    private val mqttSendUrlUseCase: MqttSendUrlUseCase,
    private val getWebEngineUseCase: GetWebEngineUseCase,
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
    private val mqttSendNetworkStateUseCase: MqttSendNetworkStateUseCase,
    private val getWebViewRefreshUseCase: GetWebViewRefreshUseCase,
    private val observeMqttFabCommandUseCase: ObserveMqttFabCommandUseCase,
    private val observeMqttScreensaverCommandUseCase: ObserveMqttScreensaverCommandUseCase,
    private val observeStreamingConfigurationUseCase: ObserveStreamingConfigurationUseCase,
    private val observeScreenStateUseCase: ObserveScreenStateUseCase,
    private val emitScreenStateUseCase: EmitScreenStateUseCase,
    private val getScreensaverUseCase: GetScreensaverUseCase,
) : ContainerHost<MainState, MainSideEffect>,
    ViewModel() {
    public override val container: Container<MainState, MainSideEffect> =
        container(MainState())

    private fun mapError(failure: Throwable): Int = when (failure) {
        is Failure.Technical.Network -> R.string.error_network
        is Failure.Technical.Database -> R.string.error_database
        is Failure.Technical.Preference -> R.string.error_preference
        is Failure.Logic.NotFound -> R.string.error_not_found
        is Failure.Logic.Business -> R.string.error_unknown
        else -> R.string.error_unknown
    }

    private var fabTimerJob: Job? = null
    private var webViewRefreshJob: Job? = null

    init {
        observeMotion()
        observeNetwork()
        observeFabCommand()
        observeScreensaverCommand()
        observeStreaming()
        observeScreenState()
    }

    private fun observeScreenState() = intent {
        observeScreenStateUseCase().collect { screenState ->
            reduce {
                when (screenState) {
                    is ScreenStateModel.Screensaver -> state.copy(isScreensaverVisible = true)
                    is ScreenStateModel.Active -> state.copy(isScreensaverVisible = false)
                }
            }
        }
    }

    private fun observeStreaming() = intent {
        observeStreamingConfigurationUseCase().collect { model ->
            reduce { state.copy(isStreamingEnabled = model?.enabled ?: false) }
        }
    }

    private fun observeMotion() = intent {
        observeMoveDetectorMotionUseCase().collect {
            if (state.isMoveDetectorEnabled) {
                onMotionDetected()
            }
        }
    }

    // Suppressed: the multiplication factor 1000 converts fabDelay (seconds) to milliseconds.
    @Suppress("MagicNumber")
    private fun onMotionDetected() = intent {
        reduce { state.copy(isFabVisible = true) }

        fabTimerJob?.cancel()
        fabTimerJob =
            intent {
                delay(state.fabDelay * 1000)
                reduce { state.copy(isFabVisible = false) }
            }
    }

    private fun observeFabCommand() = intent {
        observeMqttFabCommandUseCase().collect { isVisible ->
            if (isVisible) {
                onMotionDetected()
            } else {
                fabTimerJob?.cancel()
                reduce { state.copy(isFabVisible = false) }
            }
        }
    }

    private fun observeScreensaverCommand() = intent {
        observeMqttScreensaverCommandUseCase().collect { activate ->
            emitScreenStateUseCase(
                if (activate) ScreenStateModel.Screensaver else ScreenStateModel.Active,
            )
        }
    }

    private fun observeNetwork() = intent {
        observeNetworkStatusUseCase().collect { isAvailable ->
            if (isAvailable != state.isNetworkAvailable) {
                reduce { state.copy(isNetworkAvailable = isAvailable) }
                mqttSendNetworkStateUseCase(isAvailable)
            }
        }
    }

    private fun onLoad(): Job = executeResult(
        scope = viewModelScope,
        request = {
            val dashboard = getDashboardUseCase().getOrNull()
            val apps = loadApplicationsUseCase(chosen = true).getOrDefault(emptyList())
            val dock = getDockPositionUseCase().getOrNull()
            val moveDetector = getMoveDetectorUseCase().getOrNull()
            val webEngine = getWebEngineUseCase().getOrNull()
            val webViewRefresh = getWebViewRefreshUseCase().getOrNull()
            val screensaver = getScreensaverUseCase().getOrNull()

            Result.success(
                MainLoadResult(
                    dashboard = dashboard,
                    apps = apps,
                    dock = dock,
                    moveDetector = moveDetector,
                    webEngine = webEngine,
                    webViewRefresh = webViewRefresh,
                    screensaver = screensaver,
                ),
            )
        },
        result = { result ->
            intent {
                reduce {
                    state.copy(
                        dashboardUrls = result?.dashboard,
                        chosenApps = result?.apps ?: emptyList(),
                        dockPosition = result?.dock,
                        isMoveDetectorEnabled = result?.moveDetector?.enabled ?: false,
                        fabDelay = result?.moveDetector?.fabDelay ?: 60L,
                        webEngine = result?.webEngine ?: WebEngineModel.AndroidWebView,
                        isWebViewRefreshEnabled = result?.webViewRefresh?.enabled ?: false,
                        webViewRefreshInterval = result?.webViewRefresh?.intervalSeconds ?: 300L,
                        isScreensaverVisible = false,
                        screensaverShowClock = result?.screensaver?.showClock ?: true,
                        screensaverFolderUri = result?.screensaver?.localFolderUri,
                        screensaverSlideInterval = result?.screensaver?.slideInterval ?: 30L,
                        screensaverSource = result?.screensaver?.source ?: ScreensaverSource.BLACK,
                    )
                }
                if (state.isMoveDetectorEnabled) {
                    onMotionDetected()
                }
                restartWebViewRefreshTimer()
            }
        },
        errorBlock = { failure ->
            intent {
                postSideEffect(MainSideEffect.ShowError(mapError(failure)))
            }
        },
    )

    // Suppressed: multiplication by 1000 converts intervalSeconds to milliseconds.
    @Suppress("MagicNumber")
    private fun restartWebViewRefreshTimer() = intent {
        webViewRefreshJob?.cancel()
        if (!state.isWebViewRefreshEnabled) return@intent
        webViewRefreshJob = intent {
            while (true) {
                delay(state.webViewRefreshInterval * 1000)
                postSideEffect(MainSideEffect.ReloadWebViewEffect)
            }
        }
    }

    /**
     * Triggers navigation to the settings screen via [MainSideEffect.GoToSettingsEffect].
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onGoToSettings(): Job = intent {
        postSideEffect(MainSideEffect.GoToSettingsEffect)
    }

    /**
     * Triggers launching an external application via [MainSideEffect.OpenApplicationEffect].
     *
     * @param packageName The package name of the application to launch.
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onOpenApplication(packageName: String): Job = intent {
        postSideEffect(MainSideEffect.OpenApplicationEffect(packageName))
    }

    /**
     * Publishes the [url] of the most recently loaded WebView page to the MQTT broker.
     *
     * Failures are silently ignored — MQTT is a best-effort telemetry channel.
     *
     * @param url The fully-loaded page URL.
     * @since 0.0.2
     */
    public fun onPageLoaded(url: String): Job = intent {
        mqttSendUrlUseCase(url)
    }

    /**
     * Entry point for UI intents.
     *
     * Routes each [MainIntent] to the corresponding handler method.
     *
     * @param intent The user action to process.
     * @see MainIntent
     * @since 0.0.1
     */
    public fun handleIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.OnLoadIntent -> onLoad()
            MainIntent.OnReloadIntent -> onLoad()
            MainIntent.OnSettingsClickAction -> onGoToSettings()
            is MainIntent.OnOpenApplicationIntent -> onOpenApplication(intent.packageName)
            is MainIntent.OnPageLoadedIntent -> onPageLoaded(intent.url)
        }
    }

    private data class MainLoadResult(
        val dashboard: DashboardModel?,
        val apps: List<ApplicationModel>,
        val dock: DockPositionModel?,
        val moveDetector: MoveDetectorModel?,
        val webEngine: WebEngineModel?,
        val webViewRefresh: WebViewRefreshModel?,
        val screensaver: ScreensaverModel?,
    )
}
