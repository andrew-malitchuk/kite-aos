package domain.usecase.impl.source.usecase.mqtt

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendVolumeUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendVolumeUseCase] using [MqttRepository].
 *
 * @see MqttSendVolumeUseCase
 * @since 0.0.2
 */
@Single(binds = [MqttSendVolumeUseCase::class])
internal class MqttSendVolumeUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendVolumeUseCase {
    override suspend operator fun invoke(level: Int): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendVolume(level)
    }
}
