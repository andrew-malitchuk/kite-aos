package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.WebViewRefreshModel

/**
 * Use case for retrieving the periodic WebView refresh configuration.
 *
 * @since 0.0.6
 */
public interface GetWebViewRefreshUseCase {

    /**
     * Reads the WebView auto-refresh configuration from persistent storage.
     *
     * @return `Result.success` wrapping the current [WebViewRefreshModel] (interval and
     *   enabled flag), or `Result.failure` with a `Failure` if the preference store
     *   is unavailable.
     */
    public suspend operator fun invoke(): Result<WebViewRefreshModel>
}
