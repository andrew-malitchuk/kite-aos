package domain.usecase.api.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time changes to the screensaver configuration.
 *
 * Emits the current [ScreensaverModel] on collection and then re-emits whenever
 * the configuration is updated via [SetScreensaverUseCase]. Emits `null` when
 * no screensaver has been configured.
 *
 * @see GetScreensaverUseCase
 * @see SetScreensaverUseCase
 * @see ScreensaverModel
 * @since 0.0.7
 */
public interface ObserveScreensaverUseCase {

    /**
     * Returns a cold [Flow] backed by the preference data store.
     *
     * The flow never completes under normal operation — it terminates only when
     * the collector's coroutine scope is cancelled.
     *
     * @return A [Flow] emitting the latest [ScreensaverModel], or `null` if unconfigured.
     */
    public operator fun invoke(): Flow<ScreensaverModel?>
}
