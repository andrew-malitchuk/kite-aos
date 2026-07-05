package domain.usecase.api.source.usecase.device

import domain.core.source.model.MoveDetectorModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for updating the motion detector configuration (sensitivity, delays, etc.).
 *
 * @see GetMoveDetectorUseCase
 * @see MoveDetectorModel
 * @since 0.0.1
 */
public interface SetMoveDetectorUseCase {
    /**
     * Writes the given motion detector settings to persistent storage.
     *
     * Changes take effect the next time `MotionService` reads its configuration, which
     * typically occurs on service restart.
     *
     * @param moveDetectorModel The new motion detector configuration (sensitivity thresholds,
     *   wake/dim delays, enabled flag, etc.).
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(moveDetectorModel: MoveDetectorModel): Optional
}
