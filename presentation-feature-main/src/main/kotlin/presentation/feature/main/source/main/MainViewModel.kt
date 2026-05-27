package presentation.feature.main.source.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.core.core.execute.executeResult
import domain.core.source.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.WebEngineModel
import domain.usecase.api.source.usecase.application.LoadApplicationsUseCase
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.GetWebEngineUseCase
import domain.usecase.api.source.usecase.configuration.ObserveNetworkStatusUseCase
import domain.usecase.api.source.usecase.device.GetDockPositionUseCase
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.api.source.usecase.device.ObserveMoveDetectorMotionUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendNetworkStateUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendUrlUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendWatchdogStateUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
// Suppressed: MagicNumber for watchdog timing constants.
@Suppress("MagicNumber")
public class MainViewModel(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val loadApplicationsUseCase: LoadApplicationsUseCase,
    private val getDockPositionUseCase: GetDockPositionUseCase,
    private val getMoveDetectorUseCase: GetMoveDetectorUseCase,
    private val observeMoveDetectorMotionUseCase: ObserveMoveDetectorMotionUseCase,
    private val mqttSendUrlUseCase: MqttSendUrlUseCase,
    private val getWebEngineUseCase: GetWebEngineUseCase,
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
    private val mqttSendWatchdogStateUseCase: MqttSendWatchdogStateUseCase,
    private val mqttSendNetworkStateUseCase: MqttSendNetworkStateUseCase,
) : ContainerHost<MainState, MainSideEffect>,
    ViewModel() {
    public override val container: Container<MainState, MainSideEffect> =
        container(MainState())

    private companion object {
        /** Watchdog ping interval in milliseconds (45 s). */
        private const val WATCHDOG_INTERVAL_MS = 45_000L

        /** HTTP connection timeout for watchdog pings in milliseconds (5 s). */
        private const val WATCHDOG_TIMEOUT_MS = 5_000

        /** Auto-reload triggered after this many consecutive failures. */
        private const val WATCHDOG_RELOAD_THRESHOLD = 2

        /** WebView recreation triggered after this many consecutive failures. */
        private const val WATCHDOG_RECREATE_THRESHOLD = 4

        /** Debounce delay before reloading after network restoration (1.2 s). */
        private const val NETWORK_RESTORE_DEBOUNCE_MS = 1_200L
    }

    private fun mapError(failure: Throwable): Int = when (failure) {
        is Failure.Technical.Network -> R.string.error_network
        is Failure.Technical.Database -> R.string.error_database
        is Failure.Technical.Preference -> R.string.error_preference
        is Failure.Logic.NotFound -> R.string.error_not_found
        is Failure.Logic.Business -> R.string.error_unknown
        else -> R.string.error_unknown
    }

    private var fabTimerJob: Job? = null
    private var watchdogJob: Job? = null

    init {
        observeMotion()
        observeNetwork()
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

    private fun observeNetwork() = intent {
        observeNetworkStatusUseCase().collect { isAvailable ->
            if (isAvailable != state.isNetworkAvailable) {
                reduce { state.copy(isNetworkAvailable = isAvailable) }
                mqttSendNetworkStateUseCase(isAvailable)
                if (isAvailable && state.watchdogFailureCount > 0) {
                    // Network restored after a loss — debounce then reload.
                    delay(NETWORK_RESTORE_DEBOUNCE_MS)
                    reduce {
                        state.copy(
                            watchdogFailureCount = 0,
                            isWatchdogRecovering = false,
                            reloadTrigger = state.reloadTrigger + 1,
                        )
                    }
                    mqttSendWatchdogStateUseCase("ok")
                }
            }
        }
    }

    private fun startWatchdog(dashboardUrl: String) {
        watchdogJob?.cancel()
        watchdogJob = viewModelScope.launch {
            while (isActive) {
                delay(WATCHDOG_INTERVAL_MS)
                val isHealthy = pingUrl(dashboardUrl)
                if (isHealthy) {
                    val failureCount = container.stateFlow.value.watchdogFailureCount
                    if (failureCount > 0) {
                        intent {
                            reduce { state.copy(watchdogFailureCount = 0, isWatchdogRecovering = false) }
                        }
                        mqttSendWatchdogStateUseCase("ok")
                    }
                } else {
                    val newCount = container.stateFlow.value.watchdogFailureCount + 1
                    intent {
                        reduce { state.copy(watchdogFailureCount = newCount, isWatchdogRecovering = true) }
                        if (newCount >= WATCHDOG_RELOAD_THRESHOLD) {
                            reduce { state.copy(reloadTrigger = state.reloadTrigger + 1) }
                        }
                    }
                    mqttSendWatchdogStateUseCase("fail($newCount)")
                }
            }
        }
    }

    /**
     * Pings [url] with a HEAD request.
     *
     * Any HTTP 1xx–4xx response is treated as healthy (HA is reachable).
     * Transport errors (timeout, connection refused, DNS failure) return `false`.
     *
     * @return `true` if the endpoint is reachable, `false` on transport failure.
     */
    private fun pingUrl(url: String): Boolean = try {
        val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.connectTimeout = WATCHDOG_TIMEOUT_MS
        connection.readTimeout = WATCHDOG_TIMEOUT_MS
        connection.instanceFollowRedirects = true
        val code = connection.responseCode
        connection.disconnect()
        // Treat any HTTP response (even 4xx) as "server is alive".
        code < 500
    } catch (_: Exception) {
        false
    }

    private fun onLoad(): Job = executeResult(
        scope = viewModelScope,
        request = {
            val dashboard = getDashboardUseCase().getOrNull()
            val apps = loadApplicationsUseCase(chosen = true).getOrDefault(emptyList())
            val dock = getDockPositionUseCase().getOrNull()
            val moveDetector = getMoveDetectorUseCase().getOrNull()
            val webEngine = getWebEngineUseCase().getOrNull()

            Result.success(
                MainLoadResult(
                    dashboard = dashboard,
                    apps = apps,
                    dock = dock,
                    moveDetector = moveDetector,
                    webEngine = webEngine,
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
                    )
                }
                if (state.isMoveDetectorEnabled) {
                    onMotionDetected()
                }
                result?.dashboard?.dashboardUrl?.let { url ->
                    if (url.isNotEmpty()) startWatchdog(url)
                }
            }
        },
        errorBlock = { failure ->
            intent {
                postSideEffect(MainSideEffect.ShowError(mapError(failure)))
            }
        },
    )

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
     * Called by the UI when the WebView reports a fatal load error.
     *
     * Increments the failure counter, shows the recovery overlay, and triggers a reload
     * once [WATCHDOG_RELOAD_THRESHOLD] is reached.
     *
     * @since 0.0.5
     */
    public fun onWebViewError(): Job = intent {
        val newCount = state.watchdogFailureCount + 1
        reduce {
            state.copy(
                watchdogFailureCount = newCount,
                isWatchdogRecovering = true,
            )
        }
        mqttSendWatchdogStateUseCase("fail($newCount)")
        if (newCount >= WATCHDOG_RELOAD_THRESHOLD) {
            delay(5_000)
            reduce { state.copy(reloadTrigger = state.reloadTrigger + 1) }
        }
    }

    /**
     * Called by the UI when the WebView successfully loads a page after a previous error.
     *
     * Resets the failure counter and hides the recovery overlay.
     *
     * @since 0.0.5
     */
    public fun onWebViewRecovered(): Job = intent {
        if (state.watchdogFailureCount > 0) {
            reduce {
                state.copy(
                    watchdogFailureCount = 0,
                    isWatchdogRecovering = false,
                )
            }
            mqttSendWatchdogStateUseCase("ok")
        }
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
            MainIntent.OnWebViewErrorIntent -> onWebViewError()
            MainIntent.OnWebViewRecoveredIntent -> onWebViewRecovered()
        }
    }

    private data class MainLoadResult(
        val dashboard: DashboardModel?,
        val apps: List<ApplicationModel>,
        val dock: DockPositionModel?,
        val moveDetector: MoveDetectorModel?,
        val webEngine: WebEngineModel?,
    )
}
