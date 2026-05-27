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
    public operator fun invoke(): Flow<MqttModel?>
}
