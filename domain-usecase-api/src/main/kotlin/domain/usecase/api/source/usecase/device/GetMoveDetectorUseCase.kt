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
    public suspend operator fun invoke(): Result<MoveDetectorModel>
}
