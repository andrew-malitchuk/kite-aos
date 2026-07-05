package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for the MJPEG camera streaming configuration.
 *
 * Defines the parameters for the on-device MJPEG stream server that broadcasts
 * the camera feed over the local network. All properties are nullable to allow
 * partial updates without overwriting existing values.
 *
 * @property enabled Whether the MJPEG stream server is active.
 * @property port The TCP port on which the stream server listens.
 * @property quality The JPEG compression quality for each frame (0–100, higher = better quality).
 * @property fps The target frames per second for the stream output.
 * @property rotation The clockwise rotation applied to the camera output in degrees (0, 90, 180, 270).
 *
 * @see Model
 * @since 0.0.1
 */
public data class StreamingModel(
    val enabled: Boolean? = null,
    val port: Int? = null,
    val quality: Int? = null,
    val fps: Int? = null,
    val rotation: Int? = null,
) : Model
