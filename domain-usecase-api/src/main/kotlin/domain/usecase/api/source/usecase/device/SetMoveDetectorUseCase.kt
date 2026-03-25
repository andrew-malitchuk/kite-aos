package domain.usecase.api.source.usecase.device

import domain.core.source.model.MoveDetectorModel
import domain.usecase.api.core.common.Optional

/**
 * Use case for updating the motion detector configuration (sensitivity, delays, etc.).
 */
public interface SetMoveDetectorUseCase {
    /**
     * Updates the move detector settings.
     *
     * @param moveDetectorModel The new motion detector configuration.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(moveDetectorModel: MoveDetectorModel): Optional
}
