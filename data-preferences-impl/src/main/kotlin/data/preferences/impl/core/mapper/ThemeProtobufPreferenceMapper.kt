package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ThemePreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.ThemeDataProto

internal object ThemeProtobufPreferenceMapper :
    ProtobufPreferenceMapper<ThemeDataProto.ThemeProtoModel, ThemePreference> {
    override val toProtobuf: Mapper<ThemePreference, ThemeDataProto.ThemeProtoModel> =
        Mapper { input ->
            ThemeDataProto.ThemeProtoModel.newBuilder()
                .setMode(input.mode)
                .build()
        }

    override val toPreference: Mapper<ThemeDataProto.ThemeProtoModel, ThemePreference> =
        Mapper { input ->
            ThemePreference(
                mode = input.mode,
            )
        }
}
