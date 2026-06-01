package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.WebViewRefreshModel

/**
 * Use case for retrieving the periodic WebView refresh configuration.
 *
 * @since 0.0.6
 */
public interface GetWebViewRefreshUseCase {
    public suspend operator fun invoke(): Result<WebViewRefreshModel>
}
