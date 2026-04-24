package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.MqttModel
import domain.usecase.api.core.common.Optional

/**
 * Use case for saving the MQTT broker configuration.
 *
 * @see GetMqttConfigurationUseCase
 * @see ObserveMqttConfigurationUseCase
 * @see MqttModel
 * @since 0.0.1
 */
public interface SetMqttConfigurationUseCase {
    /**
     * Persists the [mqttModel] settings.
     *
     * @param mqttModel The new MQTT configuration.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(mqttModel: MqttModel): Optional
}
