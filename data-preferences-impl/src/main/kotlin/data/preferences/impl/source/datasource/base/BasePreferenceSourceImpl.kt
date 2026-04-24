package data.preferences.impl.source.datasource.base

import data.core.source.resource.Resource
import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A base implementation of [PreferenceSource] that orchestrates reading and writing preference data
 * through a [BasePreferenceStorage] and a [ProtobufPreferenceMapper].
 *
 * This class bridges the gap between the API-level [PreferenceSource] contract and the internal
 * Protobuf-backed DataStore storage. It delegates persistence to [storage] and applies [mapper]
 * conversions between Protobuf models and preference resources transparently.
 *
 * Concrete subclasses only need to specify the appropriate storage and mapper via constructor
 * parameters; all CRUD operations are handled by this base class.
 *
 * @param PROTO the Protobuf-generated model type used for DataStore persistence.
 * @param PREF the [Resource] preference type exposed to upper layers.
 * @param storage the underlying [BasePreferenceStorage] responsible for DataStore I/O.
 * @param mapper the [ProtobufPreferenceMapper] used to convert between [PROTO] and [PREF].
 * @see PreferenceSource
 * @see BasePreferenceStorage
 * @see ProtobufPreferenceMapper
 * @since 0.0.1
 */
internal abstract class BasePreferenceSourceImpl<PROTO : Any, PREF : Resource>(
    private val storage: BasePreferenceStorage<PROTO>,
    private val mapper: ProtobufPreferenceMapper<PROTO, PREF>,
) : PreferenceSource<PREF> {

    /**
     * Observes preference data changes by subscribing to the storage flow and mapping
     * each emitted Protobuf model to its preference resource representation.
     *
     * @return a [Flow] emitting the current [PREF] value, or `null` if no data is stored.
     */
    override fun observeData(): Flow<PREF?> = storage.subscribeToData().map { it?.let(mapper.toPreference::map) }

    /**
     * Persists the given preference data by converting it to its Protobuf representation
     * and delegating to the underlying storage.
     *
     * @param data the [PREF] value to persist, or `null` to clear the stored data.
     */
    override suspend fun setData(data: PREF?) {
        storage.updateData(data?.let(mapper.toProtobuf::map))
    }

    /**
     * Retrieves the current preference data from storage and converts it to its preference
     * resource representation.
     *
     * @return the current [PREF] value, or `null` if no data is available.
     */
    override suspend fun getData(): PREF? = storage.getData()?.let(mapper.toPreference::map)
}
