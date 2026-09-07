package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a "clear cache" button with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/button/<clientId>_clear_cache/config` as a
 * retained message. Pressing the button in Home Assistant publishes [payloadPress] to
 * [commandTopic], which the kiosk turns into a WebView cache flush followed by a reload.
 *
 * This is the one-click recovery path for a dashboard wedged on stale assets, so that fixing a
 * wall-mounted panel does not require physically reaching it.
 *
 * Only HTTP and image caches are dropped — cookies, local storage and auth sessions are preserved,
 * because clearing those would sign the panel out of Home Assistant and turn a cache flush into a
 * manual re-login.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "Clear Cache",
 *   "command_topic": "kite_abc123_clear_cache/clear_cache/press",
 *   "payload_press": "PRESS",
 *   "icon": "mdi:cached",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "config",
 *   "unique_id": "kite_abc123_clear_cache"
 * }
 * ```
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property commandTopic MQTT topic on which button presses are received.
 * @property payloadPress Payload Home Assistant publishes on press (default: `"PRESS"`).
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"config"` marks this as a maintenance
 *   action rather than a primary control.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.1.0
 */
@Serializable
internal data class ClearCacheConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Clear Cache",
    @SerialName("command_topic")
    val commandTopic: String,
    @SerialName("payload_press")
    val payloadPress: String = "PRESS",
    @SerialName("icon")
    val icon: String = "mdi:cached",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "config",
    @SerialName("unique_id")
    val uniqueId: String,
)
