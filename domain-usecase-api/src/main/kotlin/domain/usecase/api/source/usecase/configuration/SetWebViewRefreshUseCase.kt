package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional
import domain.core.source.model.WebViewRefreshModel

/**
 * Use case for persisting the periodic WebView refresh configuration.
 *
 * @since 0.0.6
 */
public interface SetWebViewRefreshUseCase {
    public suspend operator fun invoke(model: WebViewRefreshModel): Optional
}
