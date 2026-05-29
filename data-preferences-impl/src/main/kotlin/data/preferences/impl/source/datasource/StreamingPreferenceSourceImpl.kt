package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.StreamingPreferenceSource
import data.preferences.api.source.resource.StreamingPreference
import data.preferences.impl.core.mapper.StreamingProtobufPreferenceMapper
import data.preferences.impl.proto.StreamingDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.StreamingPreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [StreamingPreferenceSource::class])
internal class StreamingPreferenceSourceImpl(
    storage: StreamingPreferenceStorage,
) : BasePreferenceSourceImpl<StreamingDataProto.StreamingProtoModel, StreamingPreference>(
    storage = storage,
    mapper = StreamingProtobufPreferenceMapper,
),
    StreamingPreferenceSource
