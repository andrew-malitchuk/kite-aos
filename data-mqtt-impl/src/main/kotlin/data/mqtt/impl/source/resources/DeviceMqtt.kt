package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents device information for Home Assistant MQTT Discovery.
 *
 * @property identifiers Unique identifiers for the device.
 * @property manufacturer Name of the device manufacturer.
 * @property name Human-readable name of the device.
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
