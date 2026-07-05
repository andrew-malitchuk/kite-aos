package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting a motion detection event to the MQTT broker.
 *
 * @since 0.0.1
 */
public interface MqttSendMotionUseCase {
    /**
     * Publishes the motion detection state to the MQTT broker's motion telemetry topic.
     *
     * Typical payloads are `"ON"` for detected and `"OFF"` for cleared, following the
     * Home Assistant binary sensor convention.
     *
     * @param isDetected `true` when motion is detected, `false` when the motion window has cleared.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(isDetected: Boolean): Optional
}
