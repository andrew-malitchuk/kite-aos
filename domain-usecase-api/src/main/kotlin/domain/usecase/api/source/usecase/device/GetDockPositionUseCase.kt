package domain.usecase.api.source.usecase.device

import domain.core.source.model.DockPositionModel

/**
 * Use case for retrieving the current dock (sidebar) position.
 *
 * @see SetDockPositionUseCase
 * @see DockPositionModel
 * @since 0.0.1
 */
public interface GetDockPositionUseCase {
    /**
     * Reads the dock position preference from persistent storage.
     *
     * @return `Result.success` wrapping the current [DockPositionModel] (e.g., left or right),
     *   or `Result.failure` with a `Failure` if the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<DockPositionModel>
}
