package presentation.core.platform.source.connection

/**
 * Pure state machine driving the dashboard connection monitor.
 *
 * Deliberately free of Android types, coroutines and I/O: the clock is a parameter and the reach
 * probe is a caller-supplied boolean, so the whole `HEALTHY → SUSPECT → PAUSED → HEALTHY` cycle —
 * including the grace period and the probe interval — can be exercised with a fake clock. This is
 * the one piece of the survivability work whose behaviour is genuinely worth pinning down in tests,
 * because it is timing-dependent and its failure mode (a panel stuck paused) is silent.
 *
 * The machine decides *what should happen*; [DashboardConnectionMonitor] performs the effects.
 *
 * @param gracePeriodMillis How long an error must persist before the WebView is paused.
 * @param probeIntervalMillis How often to probe the backend while paused.
 * @since 2.2.0
 */
public class DashboardConnectionMachine(
    private val gracePeriodMillis: Long = DEFAULT_GRACE_PERIOD_MS,
    private val probeIntervalMillis: Long = DEFAULT_PROBE_INTERVAL_MS,
) {

    /** The current state. */
    public var state: DashboardConnectionState = DashboardConnectionState.HEALTHY
        private set

    /** When the current [DashboardConnectionState.SUSPECT] period began. */
    private var suspectSinceMillis: Long = 0

    /** When the backend was last probed while paused. */
    private var lastProbeMillis: Long = 0

    /**
     * Effects the caller must perform after a transition.
     *
     * @since 2.2.0
     */
    public enum class Effect {
        /** Do nothing. */
        NONE,

        /** Pause the WebView to stop its retry storm. */
        PAUSE,

        /** Resume the WebView and reload the dashboard. */
        RESUME_AND_RELOAD,

        /** Probe the backend for reachability. */
        PROBE,
    }

    /**
     * Feeds a page-load error into the machine.
     *
     * @param nowMillis Current time.
     * @return The effect to perform.
     * @since 2.2.0
     */
    public fun onLoadError(nowMillis: Long): Effect {
        if (state == DashboardConnectionState.HEALTHY) {
            state = DashboardConnectionState.SUSPECT
            suspectSinceMillis = nowMillis
        }
        return Effect.NONE
    }

    /**
     * Feeds a successful page load into the machine.
     *
     * @param nowMillis Current time.
     * @return The effect to perform — [Effect.RESUME_AND_RELOAD] only when recovering from a pause.
     * @since 2.2.0
     */
    public fun onLoadSuccess(nowMillis: Long): Effect {
        val wasPaused = state == DashboardConnectionState.PAUSED
        state = DashboardConnectionState.HEALTHY
        suspectSinceMillis = 0
        lastProbeMillis = nowMillis
        return if (wasPaused) Effect.RESUME_AND_RELOAD else Effect.NONE
    }

    /**
     * Advances the machine on a periodic tick.
     *
     * @param nowMillis Current time.
     * @return [Effect.PAUSE] when the grace period has elapsed, [Effect.PROBE] when a paused
     *   backend is due for its next reachability check, otherwise [Effect.NONE].
     * @since 2.2.0
     */
    public fun onTick(nowMillis: Long): Effect = when (state) {
        DashboardConnectionState.SUSPECT ->
            if (nowMillis - suspectSinceMillis >= gracePeriodMillis) {
                state = DashboardConnectionState.PAUSED
                lastProbeMillis = nowMillis
                Effect.PAUSE
            } else {
                Effect.NONE
            }

        DashboardConnectionState.PAUSED ->
            if (nowMillis - lastProbeMillis >= probeIntervalMillis) {
                lastProbeMillis = nowMillis
                Effect.PROBE
            } else {
                Effect.NONE
            }

        DashboardConnectionState.HEALTHY -> Effect.NONE
    }

    /**
     * Feeds a reachability probe result into the machine.
     *
     * @param reachable Whether the backend answered.
     * @param nowMillis Current time.
     * @return [Effect.RESUME_AND_RELOAD] when the backend has come back, otherwise [Effect.NONE].
     * @since 2.2.0
     */
    public fun onProbeResult(reachable: Boolean, nowMillis: Long): Effect {
        if (!reachable || state != DashboardConnectionState.PAUSED) return Effect.NONE
        state = DashboardConnectionState.HEALTHY
        suspectSinceMillis = 0
        lastProbeMillis = nowMillis
        return Effect.RESUME_AND_RELOAD
    }

    /**
     * Handles the panel waking up.
     *
     * Covers the "backend restarted while the tablet slept" case. In-process recovery timers are
     * frozen by Doze, so a panel that went to sleep in [DashboardConnectionState.PAUSED] would wake
     * still paused and stay that way until the next tick — this forces an immediate recovery
     * attempt instead.
     *
     * @param nowMillis Current time.
     * @return [Effect.RESUME_AND_RELOAD] when the last known state was not healthy.
     * @since 2.2.0
     */
    public fun onScreenWake(nowMillis: Long): Effect {
        if (state == DashboardConnectionState.HEALTHY) return Effect.NONE
        state = DashboardConnectionState.HEALTHY
        suspectSinceMillis = 0
        lastProbeMillis = nowMillis
        return Effect.RESUME_AND_RELOAD
    }

    /**
     * Default timings for [DashboardConnectionMachine].
     *
     * @since 2.2.0
     */
    public companion object {
        /**
         * Grace period before a persistent error pauses the WebView.
         *
         * Twenty seconds is comfortably longer than a Wi-Fi roam or a brief backend restart, so
         * ordinary blips never reach [DashboardConnectionState.PAUSED].
         */
        public const val DEFAULT_GRACE_PERIOD_MS: Long = 20_000L

        /**
         * Interval between reachability probes while paused.
         *
         * Ten seconds is slow enough to be invisible to a rate limiter — which is the entire point
         * of pausing — while still bringing the panel back promptly once the backend returns.
         */
        public const val DEFAULT_PROBE_INTERVAL_MS: Long = 10_000L
    }
}
