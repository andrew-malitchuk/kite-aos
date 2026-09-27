package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ResiliencePreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.ResilienceDataProto

/**
 * Bidirectional mapper between [ResilienceDataProto.ResilienceProtoModel] and [ResiliencePreference].
 *
 * This mapper is where the storage encoding is undone. proto3 scalars have no field presence, so a
 * field added in a later release reads back as its zero value for configs written before it
 * existed. Two encodings compensate, and both are reversed here so the rest of the app only ever
 * sees plain positive values:
 *
 * * **Inverted booleans** — settings that must default to ON are stored as `*_disabled`, so the
 *   zero value `false` means "not disabled". Only an explicit `false` from the preference layer
 *   writes a `true` disabled flag.
 * * **Offset hour** — `scheduled_reload_hour_plus_one` stores `hour + 1`, because `0` is a real
 *   hour (midnight) and would otherwise be indistinguishable from "never configured". A stored `0`
 *   maps back to `null`, letting the domain layer apply its own default.
 *
 * @see ResiliencePreference
 * @see ResilienceDataProto.ResilienceProtoModel
 * @see ProtobufPreferenceMapper
 * @since 2.2.0
 */
internal object ResilienceProtobufPreferenceMapper :
    ProtobufPreferenceMapper<ResilienceDataProto.ResilienceProtoModel, ResiliencePreference> {

    /** Converts a [ResiliencePreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<ResiliencePreference, ResilienceDataProto.ResilienceProtoModel> =
        Mapper { input ->
            ResilienceDataProto.ResilienceProtoModel.newBuilder()
                // Only an explicit `false` counts as opted out; null means "never chosen" = on.
                .setCrashRelaunchDisabled(input.crashRelaunchEnabled == false)
                .setScheduledReloadEnabled(input.scheduledReloadEnabled == true)
                // null hour stores 0, which reads back as null again.
                .setScheduledReloadHourPlusOne(input.scheduledReloadHour?.plus(1) ?: 0)
                .setMemoryRecoveryDisabled(input.memoryRecoveryEnabled == false)
                .setConnectionMonitorDisabled(input.connectionMonitorEnabled == false)
                .setWifiLockDisabled(input.wifiLockEnabled == false)
                .build()
        }

    /** Converts a Protobuf [ResilienceDataProto.ResilienceProtoModel] back to a [ResiliencePreference]. */
    override val toPreference: Mapper<ResilienceDataProto.ResilienceProtoModel, ResiliencePreference> =
        Mapper { input ->
            ResiliencePreference(
                crashRelaunchEnabled = !input.crashRelaunchDisabled,
                scheduledReloadEnabled = input.scheduledReloadEnabled,
                scheduledReloadHour = input.scheduledReloadHourPlusOne.takeIf { it > 0 }?.minus(1),
                memoryRecoveryEnabled = !input.memoryRecoveryDisabled,
                connectionMonitorEnabled = !input.connectionMonitorDisabled,
                wifiLockEnabled = !input.wifiLockDisabled,
            )
        }
}
