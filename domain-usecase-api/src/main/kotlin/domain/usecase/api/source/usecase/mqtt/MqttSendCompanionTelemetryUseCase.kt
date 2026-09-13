package domain.usecase.api.source.usecase.mqtt

import domain.core.source.model.CompanionTelemetryModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting the low-frequency companion telemetry to the MQTT broker.
 *
 * Unlike battery and motion, which are event-driven, these values change slowly and are sampled
 * on a periodic tick, so they are published together in a single call.
 *
 * @see CompanionTelemetryModel
 * @since 2.1.0
 */
public interface MqttSendCompanionTelemetryUseCase {
    /**
     * Publishes the sampled companion telemetry to its Home Assistant sensor topics.
     *
     * Values whose entity the user has opted out of are skipped by the data layer, so callers do
     * not need to consult the per-entity preferences before sampling.
     *
     * @param telemetry The sampled uptime, app version, IP address and RAM usage.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(telemetry: CompanionTelemetryModel): Optional
}
