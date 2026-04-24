package domain.usecase.api.source.usecase.device

import domain.core.source.model.DockPositionModel
import domain.usecase.api.core.common.Optional

/**
 * Use case for updating the dock (sidebar) position preference.
 *
 * @see GetDockPositionUseCase
 * @see DockPositionModel
 * @since 0.0.1
 */
public interface SetDockPositionUseCase {
    /**
     * @param dockPositionModel The new dock position configuration.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(dockPositionModel: DockPositionModel): Optional
}
