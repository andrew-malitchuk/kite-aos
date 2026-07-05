package domain.usecase.api.source.usecase.streaming

import domain.core.source.model.StreamingModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time changes to the camera streaming configuration.
 *
 * Emits the current [StreamingModel] on collection and then re-emits on every
 * subsequent change persisted via [SetStreamingConfigurationUseCase]. Emits `null`
 * when no streaming configuration has been saved.
 *
 * @see GetStreamingConfigurationUseCase
 * @see SetStreamingConfigurationUseCase
 * @see StreamingModel
 * @since 0.0.3
 */
public interface ObserveStreamingConfigurationUseCase {

    /**
     * Returns a cold [Flow] backed by the preference data store.
     *
     * The flow never completes under normal operation — it terminates only when
     * the collector's coroutine scope is cancelled.
     *
     * @return A [Flow] emitting the latest [StreamingModel], or `null` if unconfigured.
     */
    public operator fun invoke(): Flow<StreamingModel?>
}
