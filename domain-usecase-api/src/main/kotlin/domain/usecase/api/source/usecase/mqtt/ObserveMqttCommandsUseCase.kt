package domain.usecase.api.source.usecase.mqtt

import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing inbound MQTT commands from Home Assistant.
 *
 * Each emission is a [Pair] of (topic, payload) representing a command received from the broker.
 * The consumer is responsible for filtering by topic prefix to route commands to the
 * appropriate handler (volume, brightness, screen, app launch).
 *
 * @since 0.0.2
 */
public interface ObserveMqttCommandsUseCase {
    /**
     * Returns a [Flow] that emits every inbound MQTT command as a (topic, payload) pair.
     *
     * The flow never completes; it emits until the collector's scope is cancelled.
     */
    public operator fun invoke(): Flow<Pair<String, String>>
}
