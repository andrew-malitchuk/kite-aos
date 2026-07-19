package domain.usecase.impl.source.usecase.mqtt

import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.ObserveMqttMotionCommandUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveMqttMotionCommandUseCase] using [MqttRepository].
 *
 * Filters the shared command flow for topics ending with `_motion/motion/set` and emits a
 * pulse for each `"ON"` payload.
 *
 * @see ObserveMqttMotionCommandUseCase
 * @since 1.2.0
 */
@Single(binds = [ObserveMqttMotionCommandUseCase::class])
internal class ObserveMqttMotionCommandUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : ObserveMqttMotionCommandUseCase {
    override operator fun invoke(): Flow<Unit> = mqttRepository.observeCommands()
        .filter { (topic, payload) -> topic.endsWith("_motion/motion/set") && payload.trim().uppercase() == "ON" }
        .map { }
}
