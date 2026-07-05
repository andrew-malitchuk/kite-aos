package domain.usecase.api.source.usecase.mqtt

import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing inbound MQTT FAB visibility commands from Home Assistant.
 *
 * Each emission represents a remote request to show (`true`) or hide (`false`) the
 * Floating Action Button on the kiosk dashboard.
 *
 * @since 0.0.6
 */
public interface ObserveMqttFabCommandUseCase {
    /**
     * Returns a [Flow] that emits `true` when Home Assistant sends an `"ON"` command to the
     * FAB topic, and `false` for `"OFF"`.
     *
     * The flow never completes under normal operation — it terminates only when the
     * collector's coroutine scope is cancelled.
     *
     * @return A hot [Flow] of [Boolean] representing remote FAB visibility commands.
     */
    public operator fun invoke(): Flow<Boolean>
}
