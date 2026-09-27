package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.ResiliencePreferenceSource
import data.preferences.api.source.resource.ResiliencePreference
import data.preferences.impl.core.mapper.ResilienceProtobufPreferenceMapper
import data.preferences.impl.proto.ResilienceDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.ResiliencePreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [ResiliencePreferenceSource] backed by Proto DataStore.
 *
 * @param storage the [ResiliencePreferenceStorage] providing DataStore access.
 * @see ResiliencePreferenceSource
 * @since 2.2.0
 */
@Single(binds = [ResiliencePreferenceSource::class])
internal class ResiliencePreferenceSourceImpl(
    storage: ResiliencePreferenceStorage,
) : BasePreferenceSourceImpl<ResilienceDataProto.ResilienceProtoModel, ResiliencePreference>(
    storage = storage,
    mapper = ResilienceProtobufPreferenceMapper,
),
    ResiliencePreferenceSource
