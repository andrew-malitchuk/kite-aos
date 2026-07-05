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
     * Writes the selected browser engine to persistent storage.
     *
     * @param value The new [WebEngineModel] (e.g., Android WebView or GeckoView) to persist.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(value: WebEngineModel): Optional
}
