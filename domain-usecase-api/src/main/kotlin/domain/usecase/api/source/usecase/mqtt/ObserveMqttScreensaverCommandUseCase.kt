package domain.usecase.api.source.usecase.mqtt

import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing inbound MQTT screensaver commands from Home Assistant.
 *
 * Each emission represents a remote request to activate (`true`) or dismiss (`false`) the
 * screensaver overlay on the kiosk dashboard.
 *
 * @since 0.0.7
 */
public interface ObserveMqttScreensaverCommandUseCase {
    /**
     * Returns a [Flow] that emits `true` when Home Assistant sends an `"ON"` command to the
     * screensaver topic, and `false` for `"OFF"`.
     *
     * The flow never completes under normal operation — it terminates only when the
     * collector's coroutine scope is cancelled.
     *
     * @return A hot [Flow] of [Boolean] representing remote screensaver activation commands.
     */
    public operator fun invoke(): Flow<Boolean>
}
