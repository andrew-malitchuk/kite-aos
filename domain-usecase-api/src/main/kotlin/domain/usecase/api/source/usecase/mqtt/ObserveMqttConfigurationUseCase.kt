package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.MqttModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing changes in the MQTT configuration.
 */
public interface ObserveMqttConfigurationUseCase {
    public operator fun invoke(): Flow<MqttModel?>
}
