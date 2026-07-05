package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.WebViewRefreshPreference
import domain.core.source.model.WebViewRefreshModel
import data.repository.impl.core.mapper.base.ModelResourceMapper

/**
 * Bidirectional mapper between [WebViewRefreshModel] and [WebViewRefreshPreference].
 *
 * @see WebViewRefreshModel
 * @see WebViewRefreshPreference
 * @since 0.0.6
 */
internal object WebViewRefreshPreferenceMapper :
    ModelResourceMapper<WebViewRefreshModel, WebViewRefreshPreference> {

    override val toModel: Mapper<WebViewRefreshPreference, WebViewRefreshModel> =
        Mapper { input ->
            WebViewRefreshModel(
                enabled = input.enabled,
                intervalSeconds = input.intervalSeconds,
            )
        }

    override val toResource: Mapper<WebViewRefreshModel, WebViewRefreshPreference> =
        Mapper { input ->
            WebViewRefreshPreference(
                enabled = input.enabled,
                intervalSeconds = input.intervalSeconds,
            )
        }
}
