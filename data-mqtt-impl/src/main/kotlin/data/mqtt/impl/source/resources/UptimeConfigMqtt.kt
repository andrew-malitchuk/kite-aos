package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a device uptime sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/sensor/<clientId>_uptime/config` as a retained
 * message. The state topic carries seconds since the device last booted, sourced from
 * [android.os.SystemClock.elapsedRealtime].
 *
 * A drop in this value is the only reliable signal that a wall-mounted panel silently rebooted, so
 * `state_class` is `total_increasing` — Home Assistant then treats a decrease as a counter reset
 * rather than as a data glitch.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "device_class": "duration",
 *   "name": "Uptime",
 *   "state_topic": "kite_abc123_uptime/uptime/state",
 *   "unit_of_measurement": "s",
 *   "state_class": "total_increasing",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "diagnostic",
 *   "unique_id": "kite_abc123_uptime"
 * }
 * ```
 *
 * @property device The device information this sensor belongs to.
 * @property deviceClass The Home Assistant device class (default: `"duration"`).
 * @property name Human-readable name shown in Home Assistant.
 * @property stateTopic MQTT topic where the uptime value is published.
 * @property unitOfMeasurement Unit of the published value (default: `"s"`, seconds).
 * @property stateClass Home Assistant state class (default: `"total_increasing"`), so a reboot
 *   reads as a counter reset instead of a spurious drop.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"diagnostic"` keeps uptime off the
 *   device's primary control card.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.1.0
 */
@Serializable
internal data class UptimeConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("device_class")
    val deviceClass: String = "duration",
    @SerialName("name")
    val name: String = "Uptime",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("unit_of_measurement")
    val unitOfMeasurement: String = "s",
    @SerialName("state_class")
    val stateClass: String = "total_increasing",
    @SerialName("icon")
    val icon: String = "mdi:timer-outline",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "diagnostic",
    @SerialName("unique_id")
    val uniqueId: String,
)
