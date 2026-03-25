package domain.usecase.impl.source.usecase.mqtt

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.mqtt.GetMqttConfigurationUseCase
import domain.usecase.api.source.usecase.mqtt.MqttConnectUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttConnectUseCase] using [MqttRepository].
 *
 * This implementation first retrieves the saved configuration and only attempts to connect
 * if MQTT is enabled.
 */
@Single(binds = [MqttConnectUseCase::class])
internal class MqttConnectUseCaseImpl(
    private val mqttRepository: MqttRepository,
    private val getMqttConfigurationUseCase: GetMqttConfigurationUseCase,
) : MqttConnectUseCase {
    override suspend operator fun invoke(): Optional = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        val config = getMqttConfigurationUseCase().getOrThrow()
        if (config.enabled == true) {
            mqttRepository.connect(
                server = config.ip ?: "",
                port = config.port?.toIntOrNull() ?: 1883,
                clientId = config.clientId ?: "",
                username = config.username ?: "",
                password = config.password ?: "",
                friendlyName = config.friendlyName ?: "",
            )
        }
    }
}
