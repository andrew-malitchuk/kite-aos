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

    /**
     * Reads the MQTT broker configuration from persistent storage.
     *
     * @return `Result.success` wrapping the current [MqttModel] (host, port, credentials,
     *   client ID, etc.), or `Result.failure` with a `Failure` if the preference store
     *   is unavailable.
     */
    public suspend operator fun invoke(): Result<MqttModel>
}
