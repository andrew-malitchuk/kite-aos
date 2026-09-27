package presentation.core.platform.source.diagnostics

import android.util.Log
import domain.core.source.model.ScreenStateModel
import domain.usecase.api.source.usecase.configuration.ObserveResilienceUseCase
import domain.usecase.api.source.usecase.device.ObserveScreenStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import presentation.core.platform.source.command.RemoteCommandBus
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Turns system memory pressure into a dashboard reload, without ever blanking a screen somebody is
 * looking at.
 *
 * A reload is the cheapest way to reclaim the GPU texture and JS heap a long-lived dashboard
 * accumulates, but it is also visible: the page goes blank for a moment. So the reload is gated on
 * the screensaver being up. If pressure arrives while the screen is active the request is *held*
 * and executed the moment the panel next idles — a "stealth reload" the user never sees.
 *
 * @param observeScreenStateUseCase Source of the Active / Screensaver / DarkOverlay state.
 * @param observeResilienceUseCase Source of the user's memory-recovery opt-out.
 * @param remoteCommandBus Carries the reload request to the Main screen, which owns the engine.
 * @since 2.2.0
 */
@Single
public class MemoryRecoveryCoordinator(
    private val observeScreenStateUseCase: ObserveScreenStateUseCase,
    private val observeResilienceUseCase: ObserveResilienceUseCase,
    private val remoteCommandBus: RemoteCommandBus,
) {
    // SupervisorJob: a failure in one child coroutine does not cancel the parent or siblings.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Read from onTrimMemory (an arbitrary system thread) and from the screen-state collector, so
    // the shared flags are atomic rather than plain vars.
    private val reloadPending = AtomicBoolean(false)
    private val isEnabled = AtomicBoolean(true)
    private val isIdle = AtomicBoolean(false)

    /**
     * Starts tracking screen state and the user's opt-out.
     *
     * Call once from `Application.onCreate`.
     *
     * @since 2.2.0
     */
    public fun start() {
        scope.launch {
            observeResilienceUseCase().collect { model ->
                isEnabled.set(model?.isMemoryRecoveryOn ?: true)
            }
        }
        scope.launch {
            observeScreenStateUseCase().collect { state ->
                // Both Screensaver and DarkOverlay mean "nobody is reading the dashboard right
                // now" — DarkOverlay is the Android TV stand-in for a screen the app cannot power
                // off, so it is just as safe a moment to reload.
                val idle = state is ScreenStateModel.Screensaver || state is ScreenStateModel.DarkOverlay
                isIdle.set(idle)
                if (idle && reloadPending.compareAndSet(true, false)) {
                    Log.i(TAG, "Panel went idle; performing deferred memory-recovery reload")
                    remoteCommandBus.emitReload()
                }
            }
        }
    }

    /**
     * Records system memory pressure, reloading now or as soon as the panel next idles.
     *
     * Safe to call from any thread — [android.app.Application.onTrimMemory] does not guarantee one.
     *
     * @since 2.2.0
     */
    public fun onMemoryPressure() {
        if (!isEnabled.get()) return

        if (isIdle.get()) {
            Log.i(TAG, "Memory pressure while idle; reloading now")
            remoteCommandBus.emitReload()
        } else {
            // Deliberately not reloading here: the dashboard is on screen and a reload would blank
            // it in front of whoever is standing at the panel.
            Log.i(TAG, "Memory pressure while active; deferring reload until the panel idles")
            reloadPending.set(true)
        }
    }

    private companion object {
        private const val TAG = "MemoryRecoveryCoordinator"
    }
}
