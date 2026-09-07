package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering the "navigate to URL" text entity with Home Assistant via
 * MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/text/<clientId>_navigate/config` as a
 * retained message. Typing a URL into the entity publishes it to [commandTopic], wrapped into the
 * shared command envelope by [commandTemplate].
 *
 * No `state_topic` is declared, so the entity is optimistic. That is deliberate: the panel's real
 * URL is already published as its own sensor, and mirroring it here would break the entity
 * outright the moment a dashboard URL exceeds Home Assistant's 255-character text limit.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "Navigate To",
 *   "command_topic": "kite_abc123/command/set",
 *   "command_template": "{\"action\": \"navigate\", \"value\": \"{{ value }}\"}",
 *   "mode": "text",
 *   "max": 255,
 *   "icon": "mdi:link-variant",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "config",
 *   "unique_id": "kite_abc123_navigate"
 * }
 * ```
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic Shared remote-command topic, `{clientId}/command/set`.
 * @property commandTemplate Jinja template wrapping the typed value into the command envelope.
 * @property mode Home Assistant text input mode; `"text"` renders a plain single-line field.
 * @property max Maximum accepted length, capped at Home Assistant's own limit of 255.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"config"` keeps the field off the
 *   device's primary control card.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.2.0
 */
@Serializable
internal data class NavigateConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Navigate To",
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("command_template")
    val commandTemplate: String = """{"action": "navigate", "value": "{{ value }}"}""",
    @SerialName("mode")
    val mode: String = "text",
    @SerialName("max")
    val max: Int = 255,
    @SerialName("icon")
    val icon: String = "mdi:link-variant",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "config",
    @SerialName("unique_id")
    val uniqueId: String,
)
