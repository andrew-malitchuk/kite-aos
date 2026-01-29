package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.core.common.Optional

/**
 * Use case for initiating a connection to the configured MQTT broker.
 */
public interface MqttConnectUseCase {
    /**
     * Attempts to connect to the MQTT broker using saved credentials.
     *
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(): Optional
}
