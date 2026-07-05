package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.AutoRebootModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time changes to the auto-reboot configuration.
 *
 * Emits the current [AutoRebootModel] on collection and then re-emits on every
 * subsequent change persisted via [SetAutoRebootUseCase]. Emits `null` when the
 * stored value is absent or cleared.
 *
 * @see GetAutoRebootUseCase
 * @see SetAutoRebootUseCase
 * @see AutoRebootModel
 * @since 0.0.5
 */
public interface ObserveAutoRebootUseCase {

    /**
     * Returns a cold [Flow] backed by the preference data store.
     *
     * The flow never completes under normal operation; it terminates only when
     * the collector's coroutine scope is cancelled.
     *
     * @return A [Flow] emitting the latest [AutoRebootModel], or `null` if no
     *   configuration has been saved yet.
     */
    public operator fun invoke(): Flow<AutoRebootModel?>
}
