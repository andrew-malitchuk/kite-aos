package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.WebEngineModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for updating the selected browser engine.
 *
 * @see GetWebEngineUseCase
 * @see WebEngineModel
 * @since 0.0.4
 */
public interface SetWebEngineUseCase {
    /**
     * @param value The new [WebEngineModel] to persist.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(value: WebEngineModel): Optional
}
