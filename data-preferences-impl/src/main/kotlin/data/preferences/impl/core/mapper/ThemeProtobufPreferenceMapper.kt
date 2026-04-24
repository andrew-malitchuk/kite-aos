package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ThemePreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.ThemeDataProto

/**
 * Bidirectional mapper between [ThemeDataProto.ThemeProtoModel] and [ThemePreference].
 *
 * Handles conversion of the theme mode configuration between the Protobuf serialization format
 * used by Proto DataStore and the preference resource exposed to the data layer API.
 *
 * @see ThemePreference
 * @see ThemeDataProto.ThemeProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.ThemePreferenceSourceImpl
 * @since 0.0.1
 */
internal object ThemeProtobufPreferenceMapper :
    ProtobufPreferenceMapper<ThemeDataProto.ThemeProtoModel, ThemePreference> {

    /** Converts a [ThemePreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<ThemePreference, ThemeDataProto.ThemeProtoModel> =
        Mapper { input ->
            ThemeDataProto.ThemeProtoModel.newBuilder()
                .setMode(input.mode)
                .build()
        }

    /** Converts a Protobuf [ThemeDataProto.ThemeProtoModel] back to a [ThemePreference]. */
    override val toPreference: Mapper<ThemeDataProto.ThemeProtoModel, ThemePreference> =
        Mapper { input ->
            ThemePreference(
                mode = input.mode,
            )
        }
}
