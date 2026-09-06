package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering an app version sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/sensor/<clientId>_app_version/config` as a
 * retained message. The state topic carries the installed `versionName`, which makes a fleet of
 * panels auditable from one Home Assistant dashboard instead of by walking up to each device.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "name": "App Version",
 *   "state_topic": "kite_abc123_app_version/app_version/state",
 *   "icon": "mdi:package-variant",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "diagnostic",
 *   "unique_id": "kite_abc123_app_version"
 * }
 * ```
 *
 * @property device The device information this sensor belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property stateTopic MQTT topic where the app version string is published.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"diagnostic"` keeps the version off
 *   the device's primary control card.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.1.0
 */
@Serializable
internal data class AppVersionConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "App Version",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("icon")
    val icon: String = "mdi:package-variant",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "diagnostic",
    @SerialName("unique_id")
    val uniqueId: String,
)
