package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.core.common.Optional

/**
 * Use case for reporting the current battery level to the MQTT broker.
 *
 * @since 0.0.1
 */
public interface MqttSendBatteryLevelUseCase {
    /**
     * Publishes the [level] to the MQTT broker.
     *
     * @param level The battery level percentage (0-100).
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(level: Int): Optional
}
