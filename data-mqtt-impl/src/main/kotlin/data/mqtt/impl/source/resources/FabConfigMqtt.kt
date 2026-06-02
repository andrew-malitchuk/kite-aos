package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a FAB (Floating Action Button) visibility switch
 * with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/switch/<clientId>_fab/config` as a retained
 * message. Uses `optimistic` mode — no state topic is needed because HA assumes the state matches
 * the last command sent.
 *
 * Sending `"ON"` shows the FAB; `"OFF"` hides it.
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic MQTT topic where FAB visibility commands are received.
 * @property payloadOn Payload string for the "show FAB" command (default: `"ON"`).
 * @property payloadOff Payload string for the "hide FAB" command (default: `"OFF"`).
 * @property optimistic When `true`, HA assumes state matches the last command without a state topic.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 0.0.6
 */
@Serializable
internal data class FabConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "FAB",
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("payload_on")
    val payloadOn: String = "ON",
    @SerialName("payload_off")
    val payloadOff: String = "OFF",
    @SerialName("optimistic")
    val optimistic: Boolean = true,
    @SerialName("icon")
    val icon: String = "mdi:gesture-tap-button",
    @SerialName("unique_id")
    val uniqueId: String,
)
