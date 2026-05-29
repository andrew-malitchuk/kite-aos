package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.WebViewRefreshPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.WebViewRefreshDataProto

/**
 * Bidirectional mapper between [WebViewRefreshDataProto.WebViewRefreshProtoModel]
 * and [WebViewRefreshPreference].
 *
 * @see WebViewRefreshPreference
 * @see WebViewRefreshDataProto.WebViewRefreshProtoModel
 * @since 0.0.6
 */
internal object WebViewRefreshProtobufPreferenceMapper :
    ProtobufPreferenceMapper<WebViewRefreshDataProto.WebViewRefreshProtoModel, WebViewRefreshPreference> {

    override val toProtobuf: Mapper<WebViewRefreshPreference, WebViewRefreshDataProto.WebViewRefreshProtoModel> =
        Mapper { input ->
            WebViewRefreshDataProto.WebViewRefreshProtoModel.newBuilder()
                .setEnabled(input.enabled ?: false)
                .setIntervalSeconds(input.intervalSeconds ?: 300L)
                .build()
        }

    override val toPreference: Mapper<WebViewRefreshDataProto.WebViewRefreshProtoModel, WebViewRefreshPreference> =
        Mapper { input ->
            WebViewRefreshPreference(
                enabled = input.enabled,
                intervalSeconds = input.intervalSeconds,
            )
        }
}
