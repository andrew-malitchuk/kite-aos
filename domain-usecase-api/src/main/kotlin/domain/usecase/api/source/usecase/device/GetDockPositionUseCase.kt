package domain.usecase.api.source.usecase.device

import domain.core.source.model.DockPositionModel

/**
 * Use case for retrieving the current dock (sidebar) position.
 */
public interface GetDockPositionUseCase {
    /**
     * @return A [Result] containing the [DockPositionModel].
     */
    public suspend operator fun invoke(): Result<DockPositionModel>
}
