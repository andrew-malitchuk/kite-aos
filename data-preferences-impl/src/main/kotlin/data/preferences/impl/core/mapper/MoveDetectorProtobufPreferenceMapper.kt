package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.MoveDetectorPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.MoveDetectorDataProto

/**
 * Bidirectional mapper between [MoveDetectorDataProto.MoveDetectorProtoModel] and [MoveDetectorPreference].
 *
 * Handles conversion of motion detector configuration (enabled state, sensitivity, timing delays)
 * between the Protobuf serialization format used by Proto DataStore and the preference resource
 * exposed to the data layer API.
 *
 * Null values in [MoveDetectorPreference] are replaced with safe defaults (false for booleans,
 * 0 for numeric fields, 5L for fabDelay) when writing to Protobuf.
 *
 * @see MoveDetectorPreference
 * @see MoveDetectorDataProto.MoveDetectorProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.MoveDetectorPreferenceSourceImpl
 * @since 0.0.1
 */
internal object MoveDetectorProtobufPreferenceMapper :
    ProtobufPreferenceMapper<MoveDetectorDataProto.MoveDetectorProtoModel, MoveDetectorPreference> {

    /** Converts a [MoveDetectorPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<MoveDetectorPreference, MoveDetectorDataProto.MoveDetectorProtoModel> =
        Mapper { input ->
            MoveDetectorDataProto.MoveDetectorProtoModel.newBuilder()
                // Default null values to safe fallbacks for Protobuf serialization
                .setEnabled(input.enabled ?: false)
                .setSensitivity(input.sensitivity ?: 0)
                .setDimDelay(input.dimDelay ?: 0L)
                .setScreenTimeout(input.screenTimeout ?: 0L)
                .setFabDelay(input.fabDelay ?: 5L)
                .build()
        }

    /** Converts a Protobuf [MoveDetectorDataProto.MoveDetectorProtoModel] back to a [MoveDetectorPreference]. */
    override val toPreference: Mapper<MoveDetectorDataProto.MoveDetectorProtoModel, MoveDetectorPreference> =
        Mapper { input ->
            MoveDetectorPreference(
                enabled = input.enabled,
                // Protobuf stores sensitivity as a long; convert back to Int for the preference
                sensitivity = input.sensitivity.toInt(),
                dimDelay = input.dimDelay,
                screenTimeout = input.screenTimeout,
                fabDelay = input.fabDelay,
            )
        }
}
