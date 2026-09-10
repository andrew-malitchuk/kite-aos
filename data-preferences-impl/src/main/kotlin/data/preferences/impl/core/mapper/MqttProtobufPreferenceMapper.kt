package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.MqttPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.MqttDataProto

/**
 * Bidirectional mapper between [MqttDataProto.MqttProtoModel] and [MqttPreference].
 *
 * Handles conversion of MQTT broker connection configuration (IP, port, credentials,
 * enabled state, friendly name) between the Protobuf serialization format used by Proto
 * DataStore and the preference resource exposed to the data layer API.
 *
 * Null string fields are replaced with empty strings when writing to Protobuf, and null
 * booleans default to `false`.
 *
 * The diagnostic opt-out flags invert on the way through: the preference states them positively
 * (`uptimeEnabled`) while Protobuf stores them negatively (`uptime_disabled`), so that a config
 * written before those fields existed reads back as *enabled* rather than as off. See
 * `mqtt_data.proto` for why the storage side has to be the negative one.
 *
 * @see MqttPreference
 * @see MqttDataProto.MqttProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.MqttPreferenceSourceImpl
 * @since 0.0.1
 */
internal object MqttProtobufPreferenceMapper :
    ProtobufPreferenceMapper<MqttDataProto.MqttProtoModel, MqttPreference> {

    /** Converts a [MqttPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<MqttPreference, MqttDataProto.MqttProtoModel> =
        Mapper { input ->
            MqttDataProto.MqttProtoModel.newBuilder()
                // Default null values to empty strings / false for Protobuf serialization
                .setIp(input.ip ?: "")
                .setPort(input.port ?: "")
                .setClientId(input.clientId ?: "")
                .setUsername(input.username ?: "")
                .setPassword(input.password ?: "")
                .setEnabled(input.enabled ?: false)
                .setFriendlyName(input.friendlyName ?: "")
                // Only an explicit `false` counts as opted out; null means "never chosen" = on.
                .setUptimeDisabled(input.uptimeEnabled == false)
                .setAppVersionDisabled(input.appVersionEnabled == false)
                .setIpAddressDisabled(input.ipAddressEnabled == false)
                .setRamUsageDisabled(input.ramUsageEnabled == false)
                .build()
        }

    /** Converts a Protobuf [MqttDataProto.MqttProtoModel] back to a [MqttPreference]. */
    override val toPreference: Mapper<MqttDataProto.MqttProtoModel, MqttPreference> =
        Mapper { input ->
            MqttPreference(
                ip = input.ip,
                port = input.port,
                clientId = input.clientId,
                username = input.username,
                password = input.password,
                enabled = input.enabled,
                friendlyName = input.friendlyName,
                uptimeEnabled = !input.uptimeDisabled,
                appVersionEnabled = !input.appVersionDisabled,
                ipAddressEnabled = !input.ipAddressDisabled,
                ramUsageEnabled = !input.ramUsageDisabled,
            )
        }
}
