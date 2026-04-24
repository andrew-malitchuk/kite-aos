package data.preferences.impl.core.mapper.base

import common.core.core.mapper.Mapper
import data.core.source.resource.Resource

/**
 * A bidirectional mapper interface for converting between Protobuf models and preference [Resource] objects.
 *
 * Implementations of this interface provide two-way mapping logic to transform data between
 * the Protobuf serialization format used by DataStore and the domain-facing preference resources
 * defined in the `data-preferences-api` module.
 *
 * ```kotlin
 * object UserMapper : ProtobufPreferenceMapper<UserProtoModel, UserPreference> {
 *     override val toProtobuf = Mapper<UserPreference, UserProtoModel> { pref ->
 *         UserProtoModel.newBuilder()
 *             .setName(pref.name)
 *             .build()
 *     }
 *
 *     override val toPreference = Mapper<UserProtoModel, UserPreference> { proto ->
 *         UserPreference(name = proto.name)
 *     }
 * }
 * ```
 *
 * @param PROTOBUF the Protobuf-generated model type used for DataStore persistence.
 * @param PREFERENCE the [Resource] preference type exposed to upper layers.
 * @see common.core.core.mapper.Mapper
 * @see data.preferences.impl.source.storage.base.BasePreferenceStorage
 * @see data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
 * @since 0.0.1
 */
public interface ProtobufPreferenceMapper<PROTOBUF : Any, PREFERENCE : Resource> {

    /**
     * Mapper that converts a [PREFERENCE] resource into its [PROTOBUF] representation.
     *
     * Used when writing preference data to DataStore.
     */
    public val toProtobuf: Mapper<PREFERENCE, PROTOBUF>

    /**
     * Mapper that converts a [PROTOBUF] model into its [PREFERENCE] resource representation.
     *
     * Used when reading preference data from DataStore.
     */
    public val toPreference: Mapper<PROTOBUF, PREFERENCE>
}
