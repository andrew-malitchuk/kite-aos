package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional
import domain.core.source.model.WebViewRefreshModel

/**
 * Use case for persisting the periodic WebView refresh configuration.
 *
 * @since 0.0.6
 */
public interface SetWebViewRefreshUseCase {

    /**
     * Writes the WebView auto-refresh configuration to persistent storage.
     *
     * @param model The new refresh configuration to save (interval and enabled flag).
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(model: WebViewRefreshModel): Optional
}
