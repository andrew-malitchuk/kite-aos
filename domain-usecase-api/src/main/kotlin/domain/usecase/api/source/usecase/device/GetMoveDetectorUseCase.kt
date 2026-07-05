package domain.usecase.api.source.usecase.device

import domain.core.source.model.MoveDetectorModel

/**
 * Use case for retrieving the motion detector configuration.
 *
 * @see SetMoveDetectorUseCase
 * @see MoveDetectorModel
 * @since 0.0.1
 */
public interface GetMoveDetectorUseCase {

    /**
     * Reads the motion detector configuration from persistent storage.
     *
     * @return `Result.success` wrapping the current [MoveDetectorModel] (sensitivity,
     *   wake/dim delays, etc.), or `Result.failure` with a `Failure` if the preference
     *   store is unavailable.
     */
    public suspend operator fun invoke(): Result<MoveDetectorModel>
}
