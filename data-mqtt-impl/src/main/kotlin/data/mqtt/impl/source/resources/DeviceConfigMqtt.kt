package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a motion binary sensor with Home Assistant via MQTT Discovery.
 *
 * @property device The device information this sensor belongs to.
 * @property deviceClass The category of the device (default: "motion").
 * @property name Human-readable name of the sensor.
 * @property payloadOff Payload that represents the "off" state.
 * @property payloadOn Payload that represents the "on" state.
 * @property stateTopic MQTT topic where state updates are published.
 * @property uniqueId Unique identifier for the sensor.
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
