package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.core.common.Optional

/**
 * Use case for reporting the current media volume level to the MQTT broker.
 *
 * @since 0.0.2
 */
public interface MqttSendVolumeUseCase {
    /**
     * Publishes the [level] to the MQTT broker.
     *
     * @param level The volume level percentage (0–100).
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(level: Int): Optional
}
