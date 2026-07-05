package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.MqttModel
import domain.usecase.api.source.common.Optional

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
     * Writes the given MQTT broker settings to persistent storage.
     *
     * @param mqttModel The new MQTT configuration (host, port, client ID, credentials,
     *   topic prefix, etc.) to persist.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(mqttModel: MqttModel): Optional
}
