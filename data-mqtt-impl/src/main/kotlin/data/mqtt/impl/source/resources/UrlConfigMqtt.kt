package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a current URL sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/sensor/<clientId>_url/config`
 * as a retained message. Home Assistant uses this payload to auto-discover the URL sensor entity.
 *
 * The state is updated whenever the WebView finishes loading a new page.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "Current URL",
 *   "state_topic": "kite_abc123_url/url/state",
 *   "icon": "mdi:web",
 *   "unique_id": "kite_abc123_url"
 * }
 * ```
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property stateTopic MQTT topic where the current WebView URL is published.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 0.0.2
 */
@Serializable
internal data class UrlConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Current URL",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("icon")
    val icon: String = "mdi:web",
    @SerialName("unique_id")
    val uniqueId: String,
)
