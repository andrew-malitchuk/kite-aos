package domain.usecase.impl.source.usecase.mqtt

import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.ObserveMqttFabCommandUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveMqttFabCommandUseCase] using [MqttRepository].
 *
 * Filters the shared command flow for topics ending with `_fab/fab/set` and maps
 * the `"ON"` / `"OFF"` payload to a [Boolean].
 *
 * @see ObserveMqttFabCommandUseCase
 * @since 0.0.6
 */
@Single(binds = [ObserveMqttFabCommandUseCase::class])
internal class ObserveMqttFabCommandUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : ObserveMqttFabCommandUseCase {
    override operator fun invoke(): Flow<Boolean> = mqttRepository.observeCommands()
        .filter { (topic, _) -> topic.endsWith("_fab/fab/set") }
        .map { (_, payload) -> payload.trim().uppercase() == "ON" }
}
