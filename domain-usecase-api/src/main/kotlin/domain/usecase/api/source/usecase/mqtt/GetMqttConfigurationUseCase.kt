package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.MqttModel

/**
 * Use case for retrieving the MQTT configuration.
 *
 * @see SetMqttConfigurationUseCase
 * @see ObserveMqttConfigurationUseCase
 * @see MqttModel
 * @since 0.0.1
 */
public interface GetMqttConfigurationUseCase {
    public suspend operator fun invoke(): Result<MqttModel>
}
