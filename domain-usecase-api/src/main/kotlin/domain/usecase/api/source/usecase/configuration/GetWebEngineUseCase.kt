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
     * Reads the selected browser engine from persistent storage.
     *
     * @return `Result.success` wrapping the current [WebEngineModel], or `Result.failure`
     *   with a `Failure` if the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<WebEngineModel>
}
