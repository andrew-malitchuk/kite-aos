package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.model.MqttModel
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.ObserveMqttConfigurationUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveMqttConfigurationUseCase] using [MqttRepository].
 */
@Single(binds = [ObserveMqttConfigurationUseCase::class])
internal class ObserveMqttConfigurationUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : ObserveMqttConfigurationUseCase {
    override fun invoke(): Flow<MqttModel?> = mqttRepository.observeMqttConfiguration()
}
