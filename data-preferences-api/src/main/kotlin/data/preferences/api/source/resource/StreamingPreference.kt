package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing camera streaming server configuration.
 *
 * Controls the built-in MJPEG streaming server that allows viewing the device camera
 * feed remotely over the local network.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message StreamingProtoModel {
 *     bool enabled = 1;
 *     int32 port = 2;
 *     int32 quality = 3;
 *     int32 fps = 4;
 *     int32 rotation = 5;
 * }
 * ```
 *
 * @property enabled whether the streaming server is active.
 * @property port the TCP port on which the MJPEG server listens (e.g., 8080).
 * @property quality the JPEG compression quality for each frame (range 0–100, higher is better).
 *   Defaults to 75 when null.
 * @property fps the target frame rate for the stream in frames per second.
 *   Defaults to 10 when null.
 * @property rotation the rotation to apply to the camera output in degrees
 *   (e.g., 0, 90, 180, 270). Defaults to 0 when null.
 *
 * @see data.preferences.api.source.datasource.StreamingPreferenceSource
 *
 * @since 0.0.1
 */
public data class StreamingPreference(
    val enabled: Boolean? = null,
    val port: Int? = null,
    val quality: Int? = null,
    val fps: Int? = null,
    val rotation: Int? = null,
) : Resource
