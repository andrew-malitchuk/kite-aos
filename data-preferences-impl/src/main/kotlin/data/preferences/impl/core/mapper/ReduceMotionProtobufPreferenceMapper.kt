package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ReduceMotionPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.ReduceMotionDataProto

/**
 * Bidirectional mapper between [ReduceMotionDataProto.ReduceMotionProtoModel]
 * and [ReduceMotionPreference].
 *
 * @see ReduceMotionPreference
 * @see ReduceMotionDataProto.ReduceMotionProtoModel
 * @since 0.0.6
 */
internal object ReduceMotionProtobufPreferenceMapper :
    ProtobufPreferenceMapper<ReduceMotionDataProto.ReduceMotionProtoModel, ReduceMotionPreference> {

    /** Converts a [ReduceMotionPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<ReduceMotionPreference, ReduceMotionDataProto.ReduceMotionProtoModel> =
        Mapper { input ->
            ReduceMotionDataProto.ReduceMotionProtoModel.newBuilder()
                .setEnabled(input.isEnabled ?: false)
                .build()
        }

    /** Converts a Protobuf [ReduceMotionDataProto.ReduceMotionProtoModel] back to a [ReduceMotionPreference]. */
    override val toPreference: Mapper<ReduceMotionDataProto.ReduceMotionProtoModel, ReduceMotionPreference> =
        Mapper { input ->
            ReduceMotionPreference(isEnabled = input.enabled)
        }
}
