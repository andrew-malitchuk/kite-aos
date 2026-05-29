package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.ReduceMotionPreferenceSource
import data.preferences.api.source.resource.ReduceMotionPreference
import data.preferences.impl.core.mapper.ReduceMotionProtobufPreferenceMapper
import data.preferences.impl.proto.ReduceMotionDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.ReduceMotionPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [ReduceMotionPreferenceSource] backed by Proto DataStore.
 *
 * @param storage the [ReduceMotionPreferenceStorage] providing DataStore access.
 * @see ReduceMotionPreferenceSource
 * @since 0.0.6
 */
@Single(binds = [ReduceMotionPreferenceSource::class])
internal class ReduceMotionPreferenceSourceImpl(
    storage: ReduceMotionPreferenceStorage,
) : BasePreferenceSourceImpl<ReduceMotionDataProto.ReduceMotionProtoModel, ReduceMotionPreference>(
    storage = storage,
    mapper = ReduceMotionProtobufPreferenceMapper,
),
    ReduceMotionPreferenceSource
