package domain.usecase.impl.source.usecase.mqtt

import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.ObserveMqttClearCacheCommandUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveMqttClearCacheCommandUseCase] using [MqttRepository].
 *
 * Filters the shared command flow for topics ending with `_clear_cache/clear_cache/press`. The
 * payload is discarded: Home Assistant sends a fixed `PRESS` string, and treating any payload as
 * a press keeps a hand-published `mosquitto_pub` trigger working too.
 *
 * @see ObserveMqttClearCacheCommandUseCase
 * @since 2.1.0
 */
@Single(binds = [ObserveMqttClearCacheCommandUseCase::class])
internal class ObserveMqttClearCacheCommandUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : ObserveMqttClearCacheCommandUseCase {
    override operator fun invoke(): Flow<Unit> = mqttRepository.observeCommands()
        .filter { (topic, _) -> topic.endsWith("_clear_cache/clear_cache/press") }
        .map { }
}
