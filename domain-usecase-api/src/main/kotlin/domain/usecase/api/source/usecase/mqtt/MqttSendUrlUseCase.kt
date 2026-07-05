package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the current WebView URL to the MQTT broker.
 *
 * Called whenever the WebView finishes loading a new page.
 *
 * @since 0.0.2
 */
public interface MqttSendUrlUseCase {
    /**
     * Publishes the given URL to the broker's current-page telemetry topic.
     *
     * @param url The fully-qualified URL of the page currently loaded in the WebView.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(url: String): Optional
}
