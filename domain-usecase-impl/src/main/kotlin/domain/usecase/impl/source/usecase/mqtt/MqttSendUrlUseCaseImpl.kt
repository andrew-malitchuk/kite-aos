package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendUrlUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendUrlUseCase] using [MqttRepository].
 *
 * @see MqttSendUrlUseCase
 * @since 0.0.2
 */
@Single(binds = [MqttSendUrlUseCase::class])
internal class MqttSendUrlUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendUrlUseCase {
    override suspend operator fun invoke(url: String): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendUrl(url)
    }
}
