package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a camera stream URL sensor with Home Assistant via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/sensor/<clientId>_camera_url/config`
 * as a retained message. Home Assistant uses this payload to auto-discover the entity.
 * The state topic carries the full MJPEG stream URL (e.g., `http://192.168.1.100:8080/stream.mjpg`).
 *
 * @property device The device information this entity belongs to.
 * @property name Human-readable name shown in Home Assistant.
 * @property stateTopic MQTT topic where the current stream URL is published.
 * @property icon Material Design icon identifier shown in Home Assistant.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline`
 *   availability payload. Home Assistant marks this entity unavailable while the payload is
 *   `offline`, which the broker publishes from the MQTT Last Will when the panel drops off.
 * @property entityCategory Home Assistant entity category. `"diagnostic"` keeps this entity
 *   off the device's primary control card, where it would otherwise crowd out the actual controls.
 *
 * @see DeviceMqtt
 * @since 0.1.0
 */
@Serializable
internal data class CameraUrlConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("name")
    val name: String = "Camera Stream URL",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("icon")
    val icon: String = "mdi:cctv",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "diagnostic",
    @SerialName("unique_id")
    val uniqueId: String,
)
