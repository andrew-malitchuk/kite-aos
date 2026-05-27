package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the watchdog health state to the MQTT broker.
 *
 * Published to `{clientId}_watchdog/state` with payloads `ok`, `fail(N)`, or `recovering`.
 *
 * @since 0.0.5
 */
public interface MqttSendWatchdogStateUseCase {
    /**
     * Publishes the [state] string to the MQTT watchdog topic.
     *
     * @param state One of `"ok"`, `"fail(N)"`, or `"recovering"`.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(state: String): Optional
}
