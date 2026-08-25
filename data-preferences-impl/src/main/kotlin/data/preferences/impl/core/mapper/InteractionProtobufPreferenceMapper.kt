package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.InteractionPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.InteractionDataProto

/**
 * Bidirectional mapper between [InteractionDataProto.InteractionProtoModel] and [InteractionPreference].
 *
 * No inversion or offset encoding is needed here, unlike
 * [ResilienceProtobufPreferenceMapper]: every default in this group coincides with the proto3 zero
 * value (idle reset off at `0` minutes, gesture off at `false`). The press count is the one field
 * where `0` is not a meaningful setting, so it doubles as "not configured" and maps back to `null`.
 *
 * @see InteractionPreference
 * @see InteractionDataProto.InteractionProtoModel
 * @see ProtobufPreferenceMapper
 * @since 2.2.0
 */
internal object InteractionProtobufPreferenceMapper :
    ProtobufPreferenceMapper<InteractionDataProto.InteractionProtoModel, InteractionPreference> {

    /** Converts an [InteractionPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<InteractionPreference, InteractionDataProto.InteractionProtoModel> =
        Mapper { input ->
            InteractionDataProto.InteractionProtoModel.newBuilder()
                .setInactivityResetMinutes(input.inactivityResetMinutes ?: 0)
                .setVolumeGestureEnabled(input.volumeGestureEnabled ?: false)
                .setVolumeGesturePressCount(input.volumeGesturePressCount ?: 0)
                .build()
        }

    /** Converts a Protobuf [InteractionDataProto.InteractionProtoModel] back to an [InteractionPreference]. */
    override val toPreference: Mapper<InteractionDataProto.InteractionProtoModel, InteractionPreference> =
        Mapper { input ->
            InteractionPreference(
                inactivityResetMinutes = input.inactivityResetMinutes,
                volumeGestureEnabled = input.volumeGestureEnabled,
                volumeGesturePressCount = input.volumeGesturePressCount.takeIf { it > 0 },
            )
        }
}
