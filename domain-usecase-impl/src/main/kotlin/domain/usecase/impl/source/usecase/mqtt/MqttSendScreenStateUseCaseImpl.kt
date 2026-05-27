package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendScreenStateUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendScreenStateUseCase] using [MqttRepository].
 *
 * @see MqttSendScreenStateUseCase
 * @since 0.0.2
 */
@Single(binds = [MqttSendScreenStateUseCase::class])
internal class MqttSendScreenStateUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendScreenStateUseCase {
    override suspend operator fun invoke(isOn: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendScreenState(isOn)
    }
}
