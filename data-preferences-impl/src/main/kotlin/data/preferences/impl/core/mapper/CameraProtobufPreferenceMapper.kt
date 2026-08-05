package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.CameraPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.CameraDataProto

/**
 * Bidirectional mapper between [CameraDataProto.CameraProtoModel] and [CameraPreference].
 *
 * Converts the camera-source mode string between the Protobuf serialization format used by
 * Proto DataStore and the preference resource exposed to the data-layer API.
 *
 * @see CameraPreference
 * @see CameraDataProto.CameraProtoModel
 * @since 1.4.0
 */
internal object CameraProtobufPreferenceMapper :
    ProtobufPreferenceMapper<CameraDataProto.CameraProtoModel, CameraPreference> {

    override val toProtobuf: Mapper<CameraPreference, CameraDataProto.CameraProtoModel> =
        Mapper { input ->
            CameraDataProto.CameraProtoModel.newBuilder()
                .setMode(input.mode)
                .build()
        }

    override val toPreference: Mapper<CameraDataProto.CameraProtoModel, CameraPreference> =
        Mapper { input ->
            CameraPreference(
                mode = input.mode,
            )
        }
}
