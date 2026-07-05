package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a screensaver switch with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/switch/<clientId>_screensaver/config` as a
 * retained message. Uses `optimistic` mode — no state topic is needed because HA assumes the state
 * matches the last command sent.
 *
 * Sending `"ON"` activates the screensaver; `"OFF"` dismisses it.
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic MQTT topic where screensaver commands are received.
 * @property payloadOn Payload string for the "activate screensaver" command (default: `"ON"`).
 * @property payloadOff Payload string for the "dismiss screensaver" command (default: `"OFF"`).
 * @property optimistic When `true`, HA assumes state matches the last command without a state topic.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 0.0.7
 */
@Serializable
internal data class ScreensaverConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Screensaver",
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("payload_on")
    val payloadOn: String = "ON",
    @SerialName("payload_off")
    val payloadOff: String = "OFF",
    @SerialName("optimistic")
    val optimistic: Boolean = true,
    @SerialName("icon")
    val icon: String = "mdi:television-shimmer",
    @SerialName("unique_id")
    val uniqueId: String,
)
