package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a motion binary sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/binary_sensor/<clientId>_motion/config`
 * as a retained message. Home Assistant uses this payload to auto-discover the motion binary sensor.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "device_class": "motion",
 *   "name": "Motion",
 *   "payload_off": "OFF",
 *   "payload_on": "ON",
 *   "state_topic": "kite_abc123_motion/motion/state",
 *   "unique_id": "kite_abc123_motion"
 * }
 * ```
 *
 * @property device The device information this sensor belongs to.
 * @property deviceClass The Home Assistant device class (default: `"motion"`).
 * @property name Human-readable name of the sensor shown in Home Assistant.
 * @property payloadOff Payload string that represents the "no motion" state (default: `"OFF"`).
 * @property payloadOn Payload string that represents the "motion detected" state (default: `"ON"`).
 * @property stateTopic MQTT topic where motion state updates are published.
 * @property uniqueId Unique identifier for the sensor, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @see BatteryConfigMqtt
 * @since 0.0.1
 */
@Serializable
internal data class DeviceConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("device_class")
    val deviceClass: String = "motion",
    @SerialName("name")
    val name: String = "Motion",
    @SerialName("payload_off")
    val payloadOff: String = "OFF",
    @SerialName("payload_on")
    val payloadOn: String = "ON",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("unique_id")
    val uniqueId: String,
)
