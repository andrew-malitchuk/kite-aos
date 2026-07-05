package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for safely disconnecting from the MQTT broker.
 *
 * @see MqttConnectUseCase
 * @since 0.0.1
 */
public interface MqttDisconnectUseCase {
    /**
     * Gracefully disconnects the MQTT client and releases all associated resources.
     *
     * Safe to call even if the client is already disconnected — in that case the call
     * is a no-op. Suspends until the disconnect handshake is acknowledged by the broker.
     *
     * @return `Result.success(Unit)` when the client has been cleanly disconnected, or
     *   `Result.failure` with a `Failure.Technical.Network` if an error occurs during
     *   the disconnect procedure.
     */
    public suspend operator fun invoke(): Optional
}
