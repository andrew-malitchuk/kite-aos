package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a remote-command button with Home Assistant via
 * MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/button/<clientId>_<action>/config` as a
 * retained message. Unlike the other discovery resources this one is parameterised rather than
 * fixed to a single entity: every remote-command button publishes a different [payloadPress] to
 * the *same* [commandTopic], so the only thing that varies between them is presentation.
 *
 * [payloadPress] carries the full command JSON (e.g. `{"action":"reload"}`), which is what makes
 * the button a thin wrapper over the command topic instead of a second control path with its own
 * behaviour.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "Reload",
 *   "command_topic": "kite_abc123/command/set",
 *   "payload_press": "{\"action\":\"reload\"}",
 *   "icon": "mdi:refresh",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "config",
 *   "unique_id": "kite_abc123_reload"
 * }
 * ```
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic Shared remote-command topic, `{clientId}/command/set`.
 * @property payloadPress The command JSON published when the button is pressed.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"config"` marks this as a maintenance
 *   action rather than a primary control.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.2.0
 */
@Serializable
internal data class CommandButtonConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String,
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("payload_press")
    val payloadPress: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "config",
    @SerialName("unique_id")
    val uniqueId: String,
)
