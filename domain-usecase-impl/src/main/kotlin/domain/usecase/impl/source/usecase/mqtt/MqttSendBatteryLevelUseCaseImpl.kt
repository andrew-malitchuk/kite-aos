package domain.usecase.impl.source.usecase.mqtt

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendBatteryLevelUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendBatteryLevelUseCase] using [MqttRepository].
 */
@Single(binds = [MqttSendBatteryLevelUseCase::class])
internal class MqttSendBatteryLevelUseCaseImpl(
    private val mqttRepository: MqttRepository
) : MqttSendBatteryLevelUseCase {
    override suspend operator fun invoke(level: Int): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network
    ) {
        mqttRepository.sendBatteryLevel(level)
    }
}
