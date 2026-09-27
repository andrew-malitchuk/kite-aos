package domain.usecase.api.source.usecase.mqtt

import domain.usecase.api.source.common.Optional

/**
 * Use case for reporting dashboard-backend reachability to the MQTT broker.
 *
 * Feeds the dedicated `dashboard` binary sensor, which is intentionally separate from device
 * availability: the panel is healthy during a backend outage, and conflating the two would mark
 * the panel's own working controls unavailable.
 *
 * @since 2.2.0
 */
public interface MqttSendDashboardStateUseCase {
    /**
     * Publishes the current reachability state.
     *
     * @param isReachable `true` when the dashboard backend is answering.
     * @return `Result.success(Unit)` on successful publish, or `Result.failure` with a
     *   `Failure.Technical.Network` if the broker is unreachable or the client is not connected.
     */
    public suspend operator fun invoke(isReachable: Boolean): Optional
}
