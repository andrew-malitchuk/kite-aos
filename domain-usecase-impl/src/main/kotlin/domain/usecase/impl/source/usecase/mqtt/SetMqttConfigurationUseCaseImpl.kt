package domain.usecase.impl.source.usecase.mqtt

import domain.core.core.monad.Failure
import domain.core.source.model.MqttModel
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.mqtt.SetMqttConfigurationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetMqttConfigurationUseCase] using [MqttRepository].
 */
@Single(binds = [SetMqttConfigurationUseCase::class])
internal class SetMqttConfigurationUseCaseImpl(
    private val mqttRepository: MqttRepository
) : SetMqttConfigurationUseCase {
    override suspend fun invoke(mqttModel: MqttModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        mqttRepository.setMqttConfiguration(mqttModel)
    }
}
