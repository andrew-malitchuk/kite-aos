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
     * Publishes the network state to the MQTT broker.
     *
     * @param isOnline `true` when the network is available, `false` when lost.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(isOnline: Boolean): Optional
}
