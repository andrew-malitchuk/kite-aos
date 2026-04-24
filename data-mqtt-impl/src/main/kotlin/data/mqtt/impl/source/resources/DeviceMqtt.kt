package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents device information for Home Assistant MQTT Discovery.
 *
 * Embedded within [DeviceConfigMqtt] and [BatteryConfigMqtt] to identify the physical
 * device that owns the sensor. Home Assistant groups sensors by matching [identifiers].
 *
 * Example JSON fragment:
 * ```json
 * {
 *   "identifiers": ["kite_abc123"],
 *   "manufacturer": "Kite Kiosk",
 *   "name": "Living Room Tablet"
 * }
 * ```
 *
 * @property identifiers Unique identifiers for the device; Home Assistant uses these to group
 *   multiple sensors under one device entry.
 * @property manufacturer Name of the device manufacturer (default: `"Kite Kiosk"`).
 * @property name Human-readable name of the device shown in Home Assistant.
 *
 * @see DeviceConfigMqtt
 * @see BatteryConfigMqtt
 * @since 0.0.1
 */
@Serializable
internal data class DeviceMqtt(
    @SerialName("identifiers")
    val identifiers: List<String>,
    @SerialName("manufacturer")
    val manufacturer: String = "Kite Kiosk",
    @SerialName("name")
    val name: String = "Kite Kiosk",
)
