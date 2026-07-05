package domain.usecase.api.source.usecase.device

import domain.core.source.model.DockPositionModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for updating the dock (sidebar) position preference.
 *
 * @see GetDockPositionUseCase
 * @see DockPositionModel
 * @since 0.0.1
 */
public interface SetDockPositionUseCase {
    /**
     * Writes the given dock position preference to persistent storage.
     *
     * @param dockPositionModel The new dock position (e.g., left or right side of the screen).
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(dockPositionModel: DockPositionModel): Optional
}
