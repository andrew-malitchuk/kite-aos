package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DockPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.DockDataProto

/**
 * Bidirectional mapper between [DockDataProto.DockProtoModel] and [DockPreference].
 *
 * Handles conversion of dock position configuration between the Protobuf serialization format
 * used by Proto DataStore and the preference resource exposed to the data layer API.
 *
 * @see DockPreference
 * @see DockDataProto.DockProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.DockPositionPreferenceSourceImpl
 * @since 0.0.1
 */
internal object DockProtobufPreferenceMapper :
    ProtobufPreferenceMapper<DockDataProto.DockProtoModel, DockPreference> {

    /** Converts a [DockPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<DockPreference, DockDataProto.DockProtoModel> =
        Mapper { input ->
            DockDataProto.DockProtoModel.newBuilder()
                .setPosition(input.position)
                .build()
        }

    /** Converts a Protobuf [DockDataProto.DockProtoModel] back to a [DockPreference]. */
    override val toPreference: Mapper<DockDataProto.DockProtoModel, DockPreference> =
        Mapper { input ->
            DockPreference(
                position = input.position,
            )
        }
}
