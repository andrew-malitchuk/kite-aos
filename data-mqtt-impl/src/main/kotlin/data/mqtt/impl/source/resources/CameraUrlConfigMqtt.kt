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
    @SerialName("unique_id")
    val uniqueId: String,
)
