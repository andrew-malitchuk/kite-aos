package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the current media volume level to the MQTT broker.
 *
 * @since 0.0.2
 */
public interface MqttSendVolumeUseCase {
    /**
     * Publishes the current media volume to the broker's volume telemetry topic.
     *
     * @param level Media stream volume as a percentage in the range `0–100`.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(level: Int): Optional
}
