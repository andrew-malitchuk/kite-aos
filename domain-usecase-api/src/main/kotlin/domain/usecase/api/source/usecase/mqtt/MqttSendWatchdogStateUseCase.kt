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
     * Publishes the given watchdog health string to the broker's watchdog telemetry topic.
     *
     * @param state Watchdog health label — one of `"ok"`, `"fail(N)"` where N is the failure
     *   count, or `"recovering"`.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(state: String): Optional
}
