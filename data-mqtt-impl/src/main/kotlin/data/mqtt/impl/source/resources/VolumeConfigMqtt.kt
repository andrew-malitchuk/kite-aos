package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a volume control entity with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/number/<clientId>_volume/config`
 * as a retained message. Home Assistant uses this payload to auto-discover the volume number entity.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "Volume",
 *   "command_topic": "kite_abc123_volume/volume/set",
 *   "state_topic": "kite_abc123_volume/volume/state",
 *   "min": 0, "max": 100, "step": 1,
 *   "icon": "mdi:volume-high",
 *   "unique_id": "kite_abc123_volume"
 * }
 * ```
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic MQTT topic where volume set commands are received.
 * @property stateTopic MQTT topic where current volume state is published.
 * @property min Minimum volume value (default: `0`).
 * @property max Maximum volume value (default: `100`).
 * @property step Step size for the slider (default: `1`).
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 0.0.2
 */
@Serializable
internal data class VolumeConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Volume",
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("min")
    val min: Int = 0,
    @SerialName("max")
    val max: Int = 100,
    @SerialName("step")
    val step: Int = 1,
    @SerialName("icon")
    val icon: String = "mdi:volume-high",
    @SerialName("unique_id")
    val uniqueId: String,
)
