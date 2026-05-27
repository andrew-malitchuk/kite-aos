package domain.usecase.impl.source.usecase.mqtt

import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.ObserveMqttCommandsUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveMqttCommandsUseCase] using [MqttRepository].
 *
 * @see ObserveMqttCommandsUseCase
 * @since 0.0.2
 */
@Single(binds = [ObserveMqttCommandsUseCase::class])
internal class ObserveMqttCommandsUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : ObserveMqttCommandsUseCase {
    override operator fun invoke(): Flow<Pair<String, String>> =
        mqttRepository.observeCommands()
}
