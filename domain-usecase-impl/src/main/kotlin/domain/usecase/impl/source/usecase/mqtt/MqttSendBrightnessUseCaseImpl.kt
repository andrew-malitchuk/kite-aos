package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendBrightnessUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendBrightnessUseCase] using [MqttRepository].
 *
 * @see MqttSendBrightnessUseCase
 * @since 0.0.2
 */
@Single(binds = [MqttSendBrightnessUseCase::class])
internal class MqttSendBrightnessUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendBrightnessUseCase {
    override suspend operator fun invoke(level: Int): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendBrightness(level)
    }
}
