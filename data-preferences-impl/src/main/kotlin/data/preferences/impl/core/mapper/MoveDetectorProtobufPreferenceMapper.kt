package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.MoveDetectorPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.MoveDetectorDataProto

internal object MoveDetectorProtobufPreferenceMapper :
    ProtobufPreferenceMapper<MoveDetectorDataProto.MoveDetectorProtoModel, MoveDetectorPreference> {
    override val toProtobuf: Mapper<MoveDetectorPreference, MoveDetectorDataProto.MoveDetectorProtoModel> =
        Mapper { input ->
            MoveDetectorDataProto.MoveDetectorProtoModel.newBuilder()
                .setEnabled(input.enabled ?: false)
                .setSensitivity(input.sensitivity ?: 0)
                .setDimDelay(input.dimDelay ?: 0L)
                .setScreenTimeout(input.screenTimeout ?: 0L)
                .setFabDelay(input.fabDelay ?: 5L)
                .build()
        }

    override val toPreference: Mapper<MoveDetectorDataProto.MoveDetectorProtoModel, MoveDetectorPreference> =
        Mapper { input ->
            MoveDetectorPreference(
                enabled = input.enabled,
                sensitivity = input.sensitivity.toInt(),
                dimDelay = input.dimDelay,
                screenTimeout = input.screenTimeout,
                fabDelay = input.fabDelay,
            )
        }
}
