package domain.usecase.impl.source.usecase.mqtt

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendMotionUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendMotionUseCase] using [MqttRepository].
 *
 * @see MqttSendMotionUseCase
 * @since 0.0.1
 */
@Single(binds = [MqttSendMotionUseCase::class])
internal class MqttSendMotionUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendMotionUseCase {
    override suspend operator fun invoke(isDetected: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendMotion(isDetected)
    }
}
