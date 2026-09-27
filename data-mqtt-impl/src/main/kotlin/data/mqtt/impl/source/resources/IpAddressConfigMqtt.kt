package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering an IP address sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/sensor/<clientId>_ip_address/config` as a
 * retained message. The state topic carries the device's current LAN IPv4 address, which removes
 * the "which address is this panel on today" problem for DHCP deployments.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "IP Address",
 *   "state_topic": "kite_abc123_ip_address/ip_address/state",
 *   "icon": "mdi:ip-network",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "diagnostic",
 *   "unique_id": "kite_abc123_ip_address"
 * }
 * ```
 *
 * @property device The device information this sensor belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property stateTopic MQTT topic where the LAN IPv4 address is published.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"diagnostic"` keeps the address off
 *   the device's primary control card.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.1.0
 */
@Serializable
internal data class IpAddressConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "IP Address",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("icon")
    val icon: String = "mdi:ip-network",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "diagnostic",
    @SerialName("unique_id")
    val uniqueId: String,
)
