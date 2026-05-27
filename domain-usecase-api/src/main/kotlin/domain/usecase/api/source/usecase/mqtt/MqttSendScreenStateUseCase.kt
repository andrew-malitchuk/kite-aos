package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the current screen power state to the MQTT broker.
 *
 * @since 0.0.2
 */
public interface MqttSendScreenStateUseCase {
    /**
     * Publishes the screen state to the MQTT broker.
     *
     * @param isOn `true` if the screen is currently on (interactive), `false` otherwise.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(isOn: Boolean): Optional
}
