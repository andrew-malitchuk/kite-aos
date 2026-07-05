package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendCameraUrlUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendCameraUrlUseCase] using [MqttRepository].
 *
 * Publishes the camera stream URL over the active MQTT connection.
 * Failures are mapped to [domain.core.source.monad.Failure.Technical.Network].
 *
 * @see MqttSendCameraUrlUseCase
 * @since 0.0.2
 */
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
