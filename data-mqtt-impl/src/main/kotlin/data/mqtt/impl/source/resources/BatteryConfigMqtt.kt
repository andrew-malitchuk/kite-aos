package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a battery level sensor with Home Assistant via MQTT Discovery.
 *
 * @property device The device information this sensor belongs to.
 * @property deviceClass The category of the device (default: "battery").
 * @property name Human-readable name of the sensor.
 * @property stateTopic MQTT topic where state updates are published.
 * @property unitOfMeasurement Unit of measurement (default: "%").
 * @property uniqueId Unique identifier for the sensor.
 */
@Serializable
internal data class BatteryConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("device_class")
    val deviceClass: String = "battery",
    @SerialName("name")
    val name: String = "Battery",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("unit_of_measurement")
    val unitOfMeasurement: String = "%",
    @SerialName("unique_id")
    val uniqueId: String
)