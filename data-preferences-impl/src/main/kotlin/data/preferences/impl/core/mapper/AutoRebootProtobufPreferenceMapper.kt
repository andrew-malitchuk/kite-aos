package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.AutoRebootPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.AutoRebootDataProto

/**
 * Bidirectional mapper between [AutoRebootDataProto.AutoRebootProtoModel] and [AutoRebootPreference].
 *
 * Handles conversion of the scheduled reboot configuration (enabled state, hour, minute,
 * interval) between the Protobuf serialization format used by Proto DataStore and the
 * preference resource exposed to the data layer API.
 *
 * Null values in [AutoRebootPreference] default to `false` for booleans, `0` for hour/minute,
 * and `1` for intervalDays when writing to Protobuf.
 *
 * @see AutoRebootPreference
 * @see AutoRebootDataProto.AutoRebootProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.AutoRebootPreferenceSourceImpl
 * @since 0.0.1
 */
internal object AutoRebootProtobufPreferenceMapper :
    ProtobufPreferenceMapper<AutoRebootDataProto.AutoRebootProtoModel, AutoRebootPreference> {

    /** Converts an [AutoRebootPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<AutoRebootPreference, AutoRebootDataProto.AutoRebootProtoModel> =
        Mapper { input ->
            AutoRebootDataProto.AutoRebootProtoModel.newBuilder()
                .setEnabled(input.enabled ?: false)
                .setHour(input.hour ?: 0)
                .setMinute(input.minute ?: 0)
                .setIntervalDays(input.intervalDays ?: 1)
                .build()
        }

    /** Converts a Protobuf [AutoRebootDataProto.AutoRebootProtoModel] back to an [AutoRebootPreference]. */
    override val toPreference: Mapper<AutoRebootDataProto.AutoRebootProtoModel, AutoRebootPreference> =
        Mapper { input ->
            AutoRebootPreference(
                enabled = input.enabled,
                hour = input.hour,
                minute = input.minute,
                intervalDays = input.intervalDays,
            )
        }
}
