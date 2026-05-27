package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendWatchdogStateUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendWatchdogStateUseCase] using [MqttRepository].
 *
 * @see MqttSendWatchdogStateUseCase
 * @since 0.0.5
 */
@Single(binds = [MqttSendWatchdogStateUseCase::class])
internal class MqttSendWatchdogStateUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendWatchdogStateUseCase {
    override suspend operator fun invoke(state: String): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendWatchdogState(state)
    }
}
