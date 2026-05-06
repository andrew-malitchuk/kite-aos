package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.core.common.Optional

/**
 * Use case for reporting the current screen brightness level to the MQTT broker.
 *
 * @since 0.0.2
 */
public interface MqttSendBrightnessUseCase {
    /**
     * Publishes the [level] to the MQTT broker.
     *
     * @param level The screen brightness level (0–255, matching Android's brightness scale).
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(level: Int): Optional
}
