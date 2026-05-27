package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.WebEngineModel

/**
 * Use case for retrieving the selected browser engine.
 *
 * @see SetWebEngineUseCase
 * @see WebEngineModel
 * @since 0.0.4
 */
public interface GetWebEngineUseCase {
    /**
     * @return A [Result] containing the [WebEngineModel].
     */
    public suspend operator fun invoke(): Result<WebEngineModel>
}
