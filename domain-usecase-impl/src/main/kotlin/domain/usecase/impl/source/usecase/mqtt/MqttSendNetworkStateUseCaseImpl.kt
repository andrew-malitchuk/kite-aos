package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendNetworkStateUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendNetworkStateUseCase] using [MqttRepository].
 *
 * @see MqttSendNetworkStateUseCase
 * @since 0.0.5
 */
@Single(binds = [MqttSendNetworkStateUseCase::class])
internal class MqttSendNetworkStateUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendNetworkStateUseCase {
    override suspend operator fun invoke(isOnline: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendNetworkState(isOnline)
    }
}
