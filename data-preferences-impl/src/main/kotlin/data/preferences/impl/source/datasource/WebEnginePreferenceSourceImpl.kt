package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.WebEnginePreferenceSource
import data.preferences.api.source.resource.WebEnginePreference
import data.preferences.impl.core.mapper.WebEngineProtobufPreferenceMapper
import data.preferences.impl.proto.WebEngineDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.WebEnginePreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [WebEnginePreferenceSource] backed by Proto DataStore.
 *
 * @param webEnginePreferenceDao the [WebEnginePreferenceStorage] providing DataStore access.
 * @see WebEnginePreferenceSource
 * @see WebEnginePreferenceStorage
 * @see WebEngineProtobufPreferenceMapper
 * @since 0.0.4
 */
@Single(binds = [WebEnginePreferenceSource::class])
internal class WebEnginePreferenceSourceImpl(
    webEnginePreferenceDao: WebEnginePreferenceStorage,
) : BasePreferenceSourceImpl<WebEngineDataProto.WebEngineProtoModel, WebEnginePreference>(
    storage = webEnginePreferenceDao,
    mapper = WebEngineProtobufPreferenceMapper,
),
    WebEnginePreferenceSource
