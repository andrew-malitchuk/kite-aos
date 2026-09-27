package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for removing every Home Assistant discovery entity this device has registered.
 *
 * @see MqttDisconnectUseCase
 * @since 2.1.0
 */
public interface MqttPurgeDiscoveryUseCase {
    /**
     * Publishes an empty retained payload to each registered entity's `config` topic, which is how
     * MQTT Discovery expresses deletion.
     *
     * Invoke this when the user turns MQTT off, **before** [MqttDisconnectUseCase]: discovery
     * configs are retained messages, so simply disconnecting leaves the panel's entities in Home
     * Assistant forever with nothing left to update them.
     *
     * @return `Result.success(Unit)` once the removals have been published, or `Result.failure`
     *   with a `Failure.Technical.Network` if the broker is unreachable.
     */
    public suspend operator fun invoke(): Optional
}
