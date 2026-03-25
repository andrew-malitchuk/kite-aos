package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DockPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.DockDataProto

internal object DockProtobufPreferenceMapper :
    ProtobufPreferenceMapper<DockDataProto.DockProtoModel, DockPreference> {
    override val toProtobuf: Mapper<DockPreference, DockDataProto.DockProtoModel> =
        Mapper { input ->
            DockDataProto.DockProtoModel.newBuilder()
                .setPosition(input.position)
                .build()
        }

    override val toPreference: Mapper<DockDataProto.DockProtoModel, DockPreference> =
        Mapper { input ->
            DockPreference(
                position = input.position,
            )
        }
}
