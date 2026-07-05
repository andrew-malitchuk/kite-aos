package domain.usecase.api.source.usecase.streaming

import domain.core.source.model.StreamingModel

/**
 * Use case for retrieving the camera streaming configuration.
 *
 * @see SetStreamingConfigurationUseCase
 * @see ObserveStreamingConfigurationUseCase
 * @see StreamingModel
 * @since 0.0.3
 */
public interface GetStreamingConfigurationUseCase {

    /**
     * Reads the camera streaming configuration from persistent storage.
     *
     * @return `Result.success` wrapping the current [StreamingModel] (stream URL, resolution,
     *   credentials, etc.), or `Result.failure` with a `Failure` if the preference store
     *   is unavailable.
     */
    public suspend operator fun invoke(): Result<StreamingModel>
}
