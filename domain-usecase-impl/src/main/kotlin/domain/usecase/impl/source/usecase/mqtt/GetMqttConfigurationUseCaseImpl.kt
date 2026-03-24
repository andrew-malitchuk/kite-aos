package domain.usecase.impl.source.usecase.mqtt

import domain.core.core.monad.Failure
import domain.core.source.model.MqttModel
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.GetMqttConfigurationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetMqttConfigurationUseCase] using [MqttRepository].
 */
@Single(binds = [GetMqttConfigurationUseCase::class])
internal class GetMqttConfigurationUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : GetMqttConfigurationUseCase {
    override suspend fun invoke(): Result<MqttModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        mqttRepository.getMqttConfiguration() ?: throw Failure.Logic.NotFound
    }
}
