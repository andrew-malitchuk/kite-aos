package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the current battery level to the MQTT broker.
 *
 * @since 0.0.1
 */
public interface MqttSendBatteryLevelUseCase {
    /**
     * Publishes the current battery level to the MQTT broker's battery telemetry topic.
     *
     * @param level Battery charge percentage in the range `0–100`.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(level: Int): Optional
}
