package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.InteractionPreferenceSource
import data.preferences.api.source.resource.InteractionPreference
import data.preferences.impl.core.mapper.InteractionProtobufPreferenceMapper
import data.preferences.impl.proto.InteractionDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.InteractionPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [InteractionPreferenceSource] backed by Proto DataStore.
 *
 * @param storage the [InteractionPreferenceStorage] providing DataStore access.
 * @see InteractionPreferenceSource
 * @since 2.2.0
 */
@Single(binds = [InteractionPreferenceSource::class])
internal class InteractionPreferenceSourceImpl(
    storage: InteractionPreferenceStorage,
) : BasePreferenceSourceImpl<InteractionDataProto.InteractionProtoModel, InteractionPreference>(
    storage = storage,
    mapper = InteractionProtobufPreferenceMapper,
),
    InteractionPreferenceSource
