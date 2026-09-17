package presentation.core.platform.source.connection

import android.util.Log
import domain.usecase.api.source.usecase.configuration.ObserveResilienceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI

/**
 * Watches the dashboard backend and pauses the WebView while it is unreachable.
 *
 * **Why pausing matters.** When a Home Assistant instance restarts, its frontend JavaScript retries
 * its WebSocket roughly once per second, indefinitely. Behind a reverse proxy or an
 * intrusion-prevention layer that traffic is indistinguishable from an attack: the panel gets
 * rate-limited or its IP banned, and then stays broken *after* Home Assistant is healthy again.
 * Pausing the WebView stops the retry storm at its source. Without this the app cannot avoid the
 * problem at all, and the user's only fix is a manual reboot.
 *
 * Timing and transitions live in [DashboardConnectionMachine]; this class supplies the clock, the
 * TCP probe and the effects.
 *
 * @param observeResilienceUseCase Source of the user's opt-out.
 * @since 2.2.0
 */
@Single
public class DashboardConnectionMonitor(
    private val observeResilienceUseCase: ObserveResilienceUseCase,
) {
    // SupervisorJob: a failure in one child coroutine does not cancel the parent or siblings.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val machine = DashboardConnectionMachine()

    private var tickerJob: Job? = null
    private var isEnabled = true

    /** URL currently loaded, used to derive the probe host and port. */
    private var dashboardUrl: String? = null

    private val _state = MutableStateFlow(DashboardConnectionState.HEALTHY)

    /**
     * The current connection state, for the UI and for MQTT telemetry.
     *
     * @since 2.2.0
     */
    public val state: StateFlow<DashboardConnectionState> = _state.asStateFlow()

    private val _effects = MutableStateFlow<DashboardConnectionMachine.Effect>(
        DashboardConnectionMachine.Effect.NONE,
    )

    /**
     * The latest effect the Main screen should apply to its engine.
     *
     * Exposed as state rather than an event stream so a screen that re-subscribes (a rotation, a
     * process-death restore) immediately re-applies the standing pause instead of missing it.
     *
     * @since 2.2.0
     */
    public val effects: StateFlow<DashboardConnectionMachine.Effect> = _effects.asStateFlow()

    /**
     * Starts the monitor. Call once from `Application.onCreate`.
     *
     * @since 2.2.0
     */
    public fun start() {
        scope.launch {
            observeResilienceUseCase().collect { model ->
                isEnabled = model?.isConnectionMonitorOn ?: true
                if (isEnabled) startTicker() else stopTicker()
            }
        }
    }

    /**
     * Records the URL the dashboard is showing, so the probe knows what to connect to.
     *
     * @param url The currently loaded URL.
     * @since 2.2.0
     */
    public fun onUrlChanged(url: String) {
        dashboardUrl = url
    }

    /**
     * Reports a failed page load.
     *
     * @since 2.2.0
     */
    public fun onLoadError() {
        if (!isEnabled) return
        apply(machine.onLoadError(now()))
    }

    /**
     * Reports a successful page load.
     *
     * @since 2.2.0
     */
    public fun onLoadSuccess() {
        if (!isEnabled) return
        apply(machine.onLoadSuccess(now()))
    }

    /**
     * Reports that the panel woke up, forcing recovery if it slept while unhealthy.
     *
     * @since 2.2.0
     */
    public fun onScreenWake() {
        if (!isEnabled) return
        apply(machine.onScreenWake(now()))
    }

    /** Acknowledges the standing effect so a resume is not applied repeatedly. */
    public fun consumeEffect() {
        _effects.value = DashboardConnectionMachine.Effect.NONE
    }

    private fun startTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = scope.launch {
            while (true) {
                delay(TICK_INTERVAL_MS)
                when (val effect = machine.onTick(now())) {
                    DashboardConnectionMachine.Effect.PROBE -> {
                        val reachable = probeBackend()
                        apply(machine.onProbeResult(reachable, now()))
                    }

                    else -> apply(effect)
                }
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
        // Leaving the panel paused after the user switches the monitor off would strand it, so
        // release any standing pause on the way out.
        if (machine.state != DashboardConnectionState.HEALTHY) {
            apply(machine.onScreenWake(now()))
        }
    }

    private fun apply(effect: DashboardConnectionMachine.Effect) {
        _state.value = machine.state
        if (effect != DashboardConnectionMachine.Effect.NONE) {
            Log.i(TAG, "Connection state=${machine.state}, effect=$effect")
            _effects.value = effect
        }
    }

    /**
     * Opens a short-lived TCP connection to the dashboard host.
     *
     * A bare TCP connect is used rather than an HTTP request on purpose: it is the cheapest signal
     * that something is listening again, and it adds no request to whatever rate limiter caused the
     * outage to persist in the first place.
     */
    private suspend fun probeBackend(): Boolean = withContext(Dispatchers.IO) {
        val url = dashboardUrl ?: return@withContext false
        try {
            val uri = URI(url)
            val host = uri.host ?: return@withContext false
            val port = if (uri.port != -1) uri.port else defaultPortFor(uri.scheme)
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), PROBE_TIMEOUT_MS)
            }
            true
        } catch (e: Exception) {
            Log.d(TAG, "Backend probe failed: ${e.message}")
            false
        }
    }

    private fun defaultPortFor(scheme: String?): Int = if (scheme.equals("https", ignoreCase = true)) {
        HTTPS_PORT
    } else {
        HTTP_PORT
    }

    private fun now(): Long = System.currentTimeMillis()

    private companion object {
        private const val TAG = "DashboardConnectionMonitor"

        /**
         * Ticker period.
         *
         * Two seconds gives the machine enough resolution to honour a 20 s grace period and a 10 s
         * probe interval without waking the CPU more often than necessary.
         */
        private const val TICK_INTERVAL_MS = 2_000L
        private const val PROBE_TIMEOUT_MS = 3_000
        private const val HTTP_PORT = 80
        private const val HTTPS_PORT = 443
    }
}
