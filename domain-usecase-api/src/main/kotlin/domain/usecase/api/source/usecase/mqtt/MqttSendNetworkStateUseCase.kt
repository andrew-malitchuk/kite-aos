package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the device network connectivity state to the MQTT broker.
 *
 * Published to `{clientId}_network/state` with payloads `online` or `offline`.
 *
 * @since 0.0.5
 */
public interface MqttSendNetworkStateUseCase {
    /**
     * Publishes the current network connectivity state to the broker's network telemetry topic.
     *
     * @param isOnline `true` when a network with internet capability is available,
     *   `false` when connectivity is lost.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(isOnline: Boolean): Optional
}
