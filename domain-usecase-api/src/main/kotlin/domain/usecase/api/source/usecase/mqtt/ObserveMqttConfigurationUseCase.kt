package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.MqttModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing changes in the MQTT configuration.
 *
 * @see GetMqttConfigurationUseCase
 * @see SetMqttConfigurationUseCase
 * @see MqttModel
 * @since 0.0.1
 */
public interface ObserveMqttConfigurationUseCase {

    /**
     * Returns a cold [Flow] backed by the preference data store that re-emits on every
     * configuration change persisted via [SetMqttConfigurationUseCase].
     *
     * Emits `null` when no configuration has been saved yet. The flow never completes
     * under normal operation — it terminates only when the collector's scope is cancelled.
     *
     * @return A [Flow] emitting the latest [MqttModel], or `null` if unconfigured.
     */
    public operator fun invoke(): Flow<MqttModel?>
}
