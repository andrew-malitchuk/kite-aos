package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a RAM usage sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/sensor/<clientId>_ram_usage/config` as a
 * retained message. The state topic carries device-wide memory usage as a percentage.
 *
 * Device-wide rather than per-process usage is deliberate: it is free memory that drives Android's
 * low-memory killer, so this is the value that actually predicts the blank-page renderer kill a
 * long-running dashboard eventually hits.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "RAM Usage",
 *   "state_topic": "kite_abc123_ram_usage/ram_usage/state",
 *   "unit_of_measurement": "%",
 *   "state_class": "measurement",
 *   "icon": "mdi:memory",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "diagnostic",
 *   "unique_id": "kite_abc123_ram_usage"
 * }
 * ```
 *
 * @property device The device information this sensor belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property stateTopic MQTT topic where the memory usage percentage is published.
 * @property unitOfMeasurement Unit of the published value (default: `"%"`).
 * @property stateClass Home Assistant state class (default: `"measurement"`), enabling statistics
 *   and long-term trend graphs.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"diagnostic"` keeps RAM usage off the
 *   device's primary control card.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.1.0
 */
@Serializable
internal data class RamUsageConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "RAM Usage",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("unit_of_measurement")
    val unitOfMeasurement: String = "%",
    @SerialName("state_class")
    val stateClass: String = "measurement",
    @SerialName("icon")
    val icon: String = "mdi:memory",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "diagnostic",
    @SerialName("unique_id")
    val uniqueId: String,
)
