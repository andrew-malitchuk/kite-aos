package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing MQTT broker connection configuration.
 *
 * Stores all settings required to establish and identify an MQTT connection
 * for telemetry reporting (battery, motion events) and Home Assistant discovery.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message MqttPreference {
 *     optional bool enabled = 1;
 *     optional string ip = 2;
 *     optional string port = 3;
 *     optional string client_id = 4;
 *     optional string username = 5;
 *     optional string password = 6;
 *     optional string friendly_name = 7;
 * }
 * ```
 *
 * @property enabled whether the MQTT service is enabled.
 * @property ip the hostname or IP address of the MQTT broker.
 * @property port the port number of the MQTT broker (as a string).
 * @property clientId the unique client identifier used when connecting to the broker.
 * @property username the authentication username for the MQTT broker, or `null` if anonymous.
 * @property password the authentication password for the MQTT broker, or `null` if anonymous.
 * @property friendlyName a human-readable device name used in Home Assistant discovery payloads.
 *
 * @see data.preferences.api.source.datasource.MqttPreferenceSource
 *
 * @since 0.0.1
 */
public data class MqttPreference(
    val enabled: Boolean? = null,
    val ip: String? = null,
    val port: String? = null,
    val clientId: String? = null,
    val username: String? = null,
    val password: String? = null,
    val friendlyName: String? = null,
) : Resource
