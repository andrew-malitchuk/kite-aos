package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.WebViewRefreshPreferenceSource
import data.preferences.api.source.resource.WebViewRefreshPreference
import data.preferences.impl.core.mapper.WebViewRefreshProtobufPreferenceMapper
import data.preferences.impl.proto.WebViewRefreshDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.WebViewRefreshPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [WebViewRefreshPreferenceSource] backed by Proto DataStore.
 *
 * @param storage the [WebViewRefreshPreferenceStorage] providing DataStore access.
 * @see WebViewRefreshPreferenceSource
 * @since 0.0.6
 */
@Single(binds = [WebViewRefreshPreferenceSource::class])
internal class WebViewRefreshPreferenceSourceImpl(
    storage: WebViewRefreshPreferenceStorage,
) : BasePreferenceSourceImpl<WebViewRefreshDataProto.WebViewRefreshProtoModel, WebViewRefreshPreference>(
    storage = storage,
    mapper = WebViewRefreshProtobufPreferenceMapper,
),
    WebViewRefreshPreferenceSource
