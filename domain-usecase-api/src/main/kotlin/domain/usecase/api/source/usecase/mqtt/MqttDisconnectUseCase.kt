package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.core.common.Optional

/**
 * Use case for safely disconnecting from the MQTT broker.
 *
 * @see MqttConnectUseCase
 * @since 0.0.1
 */
public interface MqttDisconnectUseCase {
    /**
     * Disconnects the MQTT client and cleans up resources.
     *
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(): Optional
}
