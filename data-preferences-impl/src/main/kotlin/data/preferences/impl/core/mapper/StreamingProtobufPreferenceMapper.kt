package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.StreamingPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.StreamingDataProto

/**
 * Bidirectional mapper between [StreamingDataProto.StreamingProtoModel] and [StreamingPreference].
 *
 * Handles conversion of camera streaming server configuration (enabled state, port, quality,
 * frame rate, and rotation) between the Protobuf serialization format used by Proto DataStore
 * and the preference resource exposed to the data layer API.
 *
 * Null values default to `false` for enabled, `8080` for port, `75` for quality, `10` for fps,
 * and `0` for rotation when writing to Protobuf.
 *
 * @see StreamingPreference
 * @see StreamingDataProto.StreamingProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.StreamingPreferenceSourceImpl
 * @since 0.0.1
 */
internal object StreamingProtobufPreferenceMapper :
    ProtobufPreferenceMapper<StreamingDataProto.StreamingProtoModel, StreamingPreference> {

    /** Converts a [StreamingPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<StreamingPreference, StreamingDataProto.StreamingProtoModel> =
        Mapper { input ->
            StreamingDataProto.StreamingProtoModel.newBuilder()
                .setEnabled(input.enabled ?: false)
                .setPort(input.port ?: 8080)
                .setQuality(input.quality ?: 75)
                .setFps(input.fps ?: 10)
                .setRotation(input.rotation ?: 0)
                .build()
        }

    /** Converts a Protobuf [StreamingDataProto.StreamingProtoModel] back to a [StreamingPreference]. */
    override val toPreference: Mapper<StreamingDataProto.StreamingProtoModel, StreamingPreference> =
        Mapper { input ->
            StreamingPreference(
                enabled = input.enabled,
                port = input.port,
                quality = input.quality,
                fps = input.fps,
                rotation = input.rotation,
            )
        }
}
