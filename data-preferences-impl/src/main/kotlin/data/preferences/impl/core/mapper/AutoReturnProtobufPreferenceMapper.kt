package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.AutoReturnPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.AutoReturnDataProto

/**
 * Bidirectional mapper between [AutoReturnDataProto.AutoReturnProtoModel] and [AutoReturnPreference].
 *
 * @see AutoReturnPreference
 * @see AutoReturnDataProto.AutoReturnProtoModel
 * @see ProtobufPreferenceMapper
 * @since 0.0.4
 */
internal object AutoReturnProtobufPreferenceMapper :
    ProtobufPreferenceMapper<AutoReturnDataProto.AutoReturnProtoModel, AutoReturnPreference> {

    override val toProtobuf: Mapper<AutoReturnPreference, AutoReturnDataProto.AutoReturnProtoModel> =
        Mapper { input ->
            AutoReturnDataProto.AutoReturnProtoModel.newBuilder()
                .setIsEnabled(input.isEnabled ?: false)
                .build()
        }

    override val toPreference: Mapper<AutoReturnDataProto.AutoReturnProtoModel, AutoReturnPreference> =
        Mapper { input ->
            AutoReturnPreference(
                isEnabled = input.isEnabled,
            )
        }
}
