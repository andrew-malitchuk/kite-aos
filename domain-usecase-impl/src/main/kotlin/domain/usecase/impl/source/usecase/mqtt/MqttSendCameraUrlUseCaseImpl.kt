package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendCameraUrlUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

@Single(binds = [MqttSendCameraUrlUseCase::class])
internal class MqttSendCameraUrlUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendCameraUrlUseCase {
    override suspend operator fun invoke(url: String): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendCameraUrl(url)
    }
}
