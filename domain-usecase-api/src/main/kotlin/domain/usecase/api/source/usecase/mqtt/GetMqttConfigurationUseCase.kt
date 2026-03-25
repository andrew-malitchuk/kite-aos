package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.MqttModel

/**
 * Use case for retrieving the MQTT configuration.
 */
public interface GetMqttConfigurationUseCase {
    public suspend operator fun invoke(): Result<MqttModel>
}
