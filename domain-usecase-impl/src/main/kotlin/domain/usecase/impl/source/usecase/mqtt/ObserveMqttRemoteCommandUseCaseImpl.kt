package domain.usecase.impl.source.usecase.mqtt

import domain.core.source.model.RemoteCommandModel
import domain.repository.api.source.repository.MqttRepository
import domain.usecase.api.source.usecase.mqtt.ObserveMqttRemoteCommandUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

/** Suffix of the shared command topic, `{clientId}/command/set`. */
private const val COMMAND_TOPIC_SUFFIX = "/command/set"

/**
 * Wire shape of a command payload.
 *
 * `value` is nullable because the trigger-style actions (`reload`, `back`, …) carry none, and
 * unknown keys are tolerated so a payload written for a newer version is ignored rather than
 * rejected outright.
 */
@Serializable
private data class RemoteCommandPayload(
    @SerialName("action")
    val action: String = "",
    @SerialName("value")
    val value: String? = null,
)

/**
 * Implementation of [ObserveMqttRemoteCommandUseCase] using [MqttRepository].
 *
 * Filters the shared command flow down to the command topic and decodes each payload through
 * [RemoteCommandModel.of]. Anything that fails to decode is dropped: these payloads are typed by
 * hand into Home Assistant automations, so a malformed one is expected traffic, not an error
 * condition worth propagating into the collector.
 *
 * @see ObserveMqttRemoteCommandUseCase
 * @since 2.2.0
 */
@Single(binds = [ObserveMqttRemoteCommandUseCase::class])
internal class ObserveMqttRemoteCommandUseCaseImpl(
    private val mqttRepository: MqttRepository,
) : ObserveMqttRemoteCommandUseCase {

    private val json = Json { ignoreUnknownKeys = true }

    override operator fun invoke(): Flow<RemoteCommandModel> = mqttRepository.observeCommands()
        .filter { (topic, _) -> topic.endsWith(COMMAND_TOPIC_SUFFIX) }
        .mapNotNull { (_, payload) -> decode(payload) }

    private fun decode(payload: String): RemoteCommandModel? {
        val decoded = runCatching {
            json.decodeFromString<RemoteCommandPayload>(payload)
        }.getOrNull() ?: return null
        return RemoteCommandModel.of(action = decoded.action, value = decoded.value)
    }
}
