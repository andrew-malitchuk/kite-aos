package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.core.common.Optional

/**
 * Use case for reporting the current WebView URL to the MQTT broker.
 *
 * Called whenever the WebView finishes loading a new page.
 *
 * @since 0.0.2
 */
public interface MqttSendUrlUseCase {
    /**
     * Publishes the [url] to the MQTT broker.
     *
     * @param url The URL of the currently displayed page.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(url: String): Optional
}
