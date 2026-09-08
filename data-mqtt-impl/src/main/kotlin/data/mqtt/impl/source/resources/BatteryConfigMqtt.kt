package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a battery level sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/sensor/<clientId>_battery/config`
 * as a retained message. Home Assistant uses this payload to auto-discover the battery sensor.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "device_class": "battery",
 *   "name": "Battery",
 *   "state_topic": "kite_abc123_battery/battery/state",
 *   "unit_of_measurement": "%",
 *   "unique_id": "kite_abc123_battery"
 * }
 * ```
 *
 * @property device The device information this sensor belongs to.
 * @property deviceClass The Home Assistant device class (default: `"battery"`).
 * @property name Human-readable name of the sensor shown in Home Assistant.
 * @property stateTopic MQTT topic where battery level state updates are published.
 * @property unitOfMeasurement Unit of measurement for the sensor value (default: `"%"`).
 * @property uniqueId Unique identifier for the sensor, must be stable across restarts.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline`
 *   availability payload. Home Assistant marks this entity unavailable while the payload is
 *   `offline`, which the broker publishes from the MQTT Last Will when the panel drops off.
 * @property entityCategory Home Assistant entity category. `"diagnostic"` keeps this entity
 *   off the device's primary control card, where it would otherwise crowd out the actual controls.
 *
 * @see DeviceMqtt
 * @see DeviceConfigMqtt
 * @since 0.0.1
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
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "diagnostic",
    @SerialName("unique_id")
    val uniqueId: String,
)
