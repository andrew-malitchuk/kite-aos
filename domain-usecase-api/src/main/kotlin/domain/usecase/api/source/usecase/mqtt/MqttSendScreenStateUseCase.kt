package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the current screen power state to the MQTT broker.
 *
 * @since 0.0.2
 */
public interface MqttSendScreenStateUseCase {
    /**
     * Publishes the current screen power state to the broker's screen telemetry topic.
     *
     * @param isOn `true` when the screen is on and interactive, `false` when it is off or locked.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(isOn: Boolean): Optional
}
