package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.AutoRebootPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.AutoRebootDataProto

internal object AutoRebootProtobufPreferenceMapper :
    ProtobufPreferenceMapper<AutoRebootDataProto.AutoRebootProtoModel, AutoRebootPreference> {

    override val toProtobuf: Mapper<AutoRebootPreference, AutoRebootDataProto.AutoRebootProtoModel> =
        Mapper { input ->
            AutoRebootDataProto.AutoRebootProtoModel.newBuilder()
                .setEnabled(input.enabled ?: false)
                .setHour(input.hour ?: 0)
                .setMinute(input.minute ?: 0)
                .setIntervalDays(input.intervalDays ?: 1)
                .build()
        }

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
