package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttSendDashboardStateUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttSendDashboardStateUseCase] using [MqttRepository].
 *
 * @see MqttSendDashboardStateUseCase
 * @since 2.2.0
 */
@Single(binds = [MqttSendDashboardStateUseCase::class])
internal class MqttSendDashboardStateUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttSendDashboardStateUseCase {
    override suspend operator fun invoke(isReachable: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.sendDashboardState(isReachable)
    }
}
