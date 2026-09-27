package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.mqtt.MqttPurgeDiscoveryUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttPurgeDiscoveryUseCase] using [MqttRepository].
 *
 * @see MqttPurgeDiscoveryUseCase
 * @since 2.1.0
 */
@Single(binds = [MqttPurgeDiscoveryUseCase::class])
internal class MqttPurgeDiscoveryUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : MqttPurgeDiscoveryUseCase {
    override suspend operator fun invoke(): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        mqttRepository.purgeDiscovery()
    }
}
