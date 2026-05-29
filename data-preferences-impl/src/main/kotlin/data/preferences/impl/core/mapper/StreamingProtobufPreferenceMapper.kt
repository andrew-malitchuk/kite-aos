package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.StreamingPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.StreamingDataProto

internal object StreamingProtobufPreferenceMapper :
    ProtobufPreferenceMapper<StreamingDataProto.StreamingProtoModel, StreamingPreference> {

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
