package domain.usecase.api.source.usecase.streaming

import domain.core.source.model.StreamingModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the camera streaming configuration.
 *
 * @see GetStreamingConfigurationUseCase
 * @see ObserveStreamingConfigurationUseCase
 * @see StreamingModel
 * @since 0.0.3
 */
public interface SetStreamingConfigurationUseCase {

    /**
     * Writes the given streaming configuration to persistent storage.
     *
     * @param streaming The new streaming settings (stream URL, resolution, credentials, etc.)
     *   to persist.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(streaming: StreamingModel): Optional
}
