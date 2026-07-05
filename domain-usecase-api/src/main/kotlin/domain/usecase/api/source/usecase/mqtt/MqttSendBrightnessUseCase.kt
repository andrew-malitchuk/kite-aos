package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the current screen brightness level to the MQTT broker.
 *
 * @since 0.0.2
 */
public interface MqttSendBrightnessUseCase {
    /**
     * Publishes the current screen brightness to the MQTT broker's brightness telemetry topic.
     *
     * @param level Screen brightness in Android units (`0–255`), where `0` is minimum brightness
     *   and `255` is maximum brightness.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(level: Int): Optional
}
