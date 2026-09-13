package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.RemoteCommandModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing inbound remote WebView commands published to the MQTT command topic.
 *
 * Unlike the single-purpose command use cases (FAB, screensaver, clear cache), this one carries a
 * whole vocabulary on one topic: `{clientId}/command/set`, payload
 * `{ "action": "navigate", "value": "https://…" }`. One topic keeps a Home Assistant automation
 * or a `mosquitto_pub` one-liner from having to know a different topic per action, and it is the
 * surface later transports (a local HTTP control API) are meant to decode into.
 *
 * Payloads that are not valid JSON, name an unknown action, or omit a value the action needs are
 * dropped silently — a typo in an automation must not take the command channel down with it.
 *
 * @see RemoteCommandModel
 * @since 2.2.0
 */
public interface ObserveMqttRemoteCommandUseCase {
    /**
     * Returns a [Flow] that emits one [RemoteCommandModel] per well-formed inbound command.
     *
     * The flow never completes under normal operation — it terminates only when the collector's
     * coroutine scope is cancelled.
     *
     * @return A hot [Flow] of decoded remote commands.
     */
    public operator fun invoke(): Flow<RemoteCommandModel>
}
