package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a screen on/off switch with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/switch/<clientId>_screen/config`
 * as a retained message. Home Assistant uses this payload to auto-discover the screen switch entity.
 *
 * Sending `"ON"` to the command topic wakes the device; `"OFF"` locks it via Device Policy Manager.
 * State changes (screen on/off events) are published automatically to the state topic.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "Screen",
 *   "command_topic": "kite_abc123_screen/screen/set",
 *   "state_topic": "kite_abc123_screen/screen/state",
 *   "payload_on": "ON", "payload_off": "OFF",
 *   "state_on": "ON", "state_off": "OFF",
 *   "icon": "mdi:monitor",
 *   "unique_id": "kite_abc123_screen"
 * }
 * ```
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic MQTT topic where screen on/off commands are received.
 * @property stateTopic MQTT topic where current screen state is published.
 * @property payloadOn Payload string for the "screen on" command (default: `"ON"`).
 * @property payloadOff Payload string for the "screen off" command (default: `"OFF"`).
 * @property stateOn State string representing "screen on" (default: `"ON"`).
 * @property stateOff State string representing "screen off" (default: `"OFF"`).
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 0.0.2
 */
@Serializable
internal data class ScreenConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Screen",
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("payload_on")
    val payloadOn: String = "ON",
    @SerialName("payload_off")
    val payloadOff: String = "OFF",
    @SerialName("state_on")
    val stateOn: String = "ON",
    @SerialName("state_off")
    val stateOff: String = "OFF",
    @SerialName("icon")
    val icon: String = "mdi:monitor",
    @SerialName("unique_id")
    val uniqueId: String,
)
