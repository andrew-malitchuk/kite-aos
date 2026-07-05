package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the active camera stream URL to the MQTT broker.
 *
 * Published when the streaming configuration changes so that Home Assistant
 * can display the camera feed without polling the device.
 *
 * @since 0.0.3
 */
public interface MqttSendCameraUrlUseCase {

    /**
     * Publishes the [url] of the active camera stream to the MQTT broker.
     *
     * @param url The RTSP or HTTP camera stream URL to publish. An empty string signals
     *   that no stream is active.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(url: String): Optional
}
