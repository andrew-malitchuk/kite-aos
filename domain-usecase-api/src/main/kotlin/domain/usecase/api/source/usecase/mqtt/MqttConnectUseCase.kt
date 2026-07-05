package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for initiating a connection to the configured MQTT broker.
 *
 * @see MqttDisconnectUseCase
 * @since 0.0.1
 */
public interface MqttConnectUseCase {
    /**
     * Attempts to connect to the MQTT broker using saved credentials from [GetMqttConfigurationUseCase].
     *
     * The call suspends until the connection handshake completes or fails. After a successful
     * connection the client can publish and subscribe to topics.
     *
     * @return `Result.success(Unit)` when the broker accepts the connection, or `Result.failure`
     *   with a `Failure.Technical.Network` if the broker is unreachable, credentials are
     *   rejected, or no configuration has been saved.
     */
    public suspend operator fun invoke(): Optional
}
