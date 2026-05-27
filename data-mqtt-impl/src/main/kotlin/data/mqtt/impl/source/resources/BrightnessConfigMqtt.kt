package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a screen brightness control entity with Home Assistant
 * via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/number/<clientId>_brightness/config`
 * as a retained message. Home Assistant uses this payload to auto-discover the brightness number entity.
 *
 * The brightness range matches Android's [android.provider.Settings.System.SCREEN_BRIGHTNESS] (0–255).
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "Brightness",
 *   "command_topic": "kite_abc123_brightness/brightness/set",
 *   "state_topic": "kite_abc123_brightness/brightness/state",
 *   "min": 0, "max": 255, "step": 1,
 *   "icon": "mdi:brightness-6",
 *   "unique_id": "kite_abc123_brightness"
 * }
 * ```
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic MQTT topic where brightness set commands are received.
 * @property stateTopic MQTT topic where current brightness state is published.
 * @property min Minimum brightness value (default: `0`).
 * @property max Maximum brightness value (default: `255`, matching Android's brightness scale).
 * @property step Step size for the slider (default: `1`).
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 0.0.2
 */
@Serializable
internal data class BrightnessConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Brightness",
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("min")
    val min: Int = 0,
    @SerialName("max")
    val max: Int = 255,
    @SerialName("step")
    val step: Int = 1,
    @SerialName("icon")
    val icon: String = "mdi:brightness-6",
    @SerialName("unique_id")
    val uniqueId: String,
)
