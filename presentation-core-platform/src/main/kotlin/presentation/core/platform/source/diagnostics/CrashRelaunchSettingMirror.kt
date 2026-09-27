package presentation.core.platform.source.diagnostics

import domain.usecase.api.source.usecase.configuration.ObserveResilienceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/**
 * Mirrors the crash-relaunch user setting from Proto DataStore into [CrashDiagnosticsStore].
 *
 * This exists because of a hard constraint in the crash path: [CrashRelaunchHandler] runs in a
 * process that is being torn down, where a `suspend` DataStore read cannot be relied on to
 * complete. Reading the flag there is therefore not an option, so the value is kept continuously
 * up to date in a synchronous [android.content.SharedPreferences] mirror instead, and the crash
 * handler reads that.
 *
 * @param observeResilienceUseCase Source of truth for the setting.
 * @param store Destination mirror consulted at crash time.
 * @see CrashRelaunchHandler
 * @since 2.2.0
 */
@Single
public class CrashRelaunchSettingMirror(
    private val observeResilienceUseCase: ObserveResilienceUseCase,
    private val store: CrashDiagnosticsStore,
) {
    // SupervisorJob: a failure in one child coroutine does not cancel the parent or siblings.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Starts mirroring. Intended to be called once from `Application.onCreate`.
     *
     * @since 2.2.0
     */
    public fun start() {
        scope.launch {
            observeResilienceUseCase().collect { model ->
                store.isRelaunchEnabled = model?.isCrashRelaunchOn ?: true
            }
        }
    }
}
