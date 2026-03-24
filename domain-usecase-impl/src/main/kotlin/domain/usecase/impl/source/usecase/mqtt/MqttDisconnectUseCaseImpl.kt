package domain.usecase.impl.source.usecase.mqtt

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttDisconnectUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttDisconnectUseCase] using [MqttRepository].
 */
@Single(binds = [MqttDisconnectUseCase::class])
internal class MqttDisconnectUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttDisconnectUseCase {
    override suspend operator fun invoke(): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.disconnect()
    }
}
