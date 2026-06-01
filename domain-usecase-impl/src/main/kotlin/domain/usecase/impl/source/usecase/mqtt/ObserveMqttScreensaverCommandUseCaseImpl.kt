package domain.usecase.impl.source.usecase.mqtt

import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.ObserveMqttScreensaverCommandUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [ObserveMqttScreensaverCommandUseCase::class])
internal class ObserveMqttScreensaverCommandUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : ObserveMqttScreensaverCommandUseCase {
    override operator fun invoke(): Flow<Boolean> = mqttRepository.observeCommands()
        .filter { (topic, _) -> topic.endsWith("_screensaver/screensaver/set") }
        .map { (_, payload) -> payload.trim().uppercase() == "ON" }
}
