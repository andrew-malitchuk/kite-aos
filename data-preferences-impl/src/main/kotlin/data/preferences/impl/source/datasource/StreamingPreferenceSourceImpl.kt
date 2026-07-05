package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.StreamingPreferenceSource
import data.preferences.api.source.resource.StreamingPreference
import data.preferences.impl.core.mapper.StreamingProtobufPreferenceMapper
import data.preferences.impl.proto.StreamingDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.StreamingPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [StreamingPreferenceSource] backed by Proto DataStore.
 *
 * Delegates persistence to [StreamingPreferenceStorage] and uses
 * [StreamingProtobufPreferenceMapper] for converting between the Protobuf model and the
 * [StreamingPreference] resource.
 *
 * @param storage the [StreamingPreferenceStorage] providing DataStore access.
 * @see StreamingPreferenceSource
 * @see StreamingPreferenceStorage
 * @see StreamingProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 0.0.1
 */
@Single(binds = [StreamingPreferenceSource::class])
internal class StreamingPreferenceSourceImpl(
    storage: StreamingPreferenceStorage,
) : BasePreferenceSourceImpl<StreamingDataProto.StreamingProtoModel, StreamingPreference>(
    storage = storage,
    mapper = StreamingProtobufPreferenceMapper,
),
    StreamingPreferenceSource
