package data.preferences.impl.source.datasource.base

import data.core.source.resource.Resource
import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A base implementation of [PreferenceSource] that orchestrates [BasePreferenceStorage] and [ProtobufPreferenceMapper].
 *
 * @param PROTO The protobuf type used in storage.
 * @param PREF The [Resource] preference type.
 * @param storage The underlying storage for protobuf models.
 * @param mapper The mapper for converting between protobuf and preference objects.
 */
internal abstract class BasePreferenceSourceImpl<PROTO : Any, PREF : Resource>(
    private val storage: BasePreferenceStorage<PROTO>,
    private val mapper: ProtobufPreferenceMapper<PROTO, PREF>,
) : PreferenceSource<PREF> {
    override fun observeData(): Flow<PREF?> = storage.subscribeToData().map { it?.let(mapper.toPreference::map) }

    override suspend fun setData(data: PREF?) {
        storage.updateData(data?.let(mapper.toProtobuf::map))
    }

    override suspend fun getData(): PREF? = storage.getData()?.let(mapper.toPreference::map)
}
