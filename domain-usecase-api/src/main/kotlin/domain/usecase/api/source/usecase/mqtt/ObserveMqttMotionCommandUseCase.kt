package domain.usecase.api.source.usecase.mqtt

import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing inbound MQTT motion commands from Home Assistant.
 *
 * Lets an external presence source (e.g. a PIR sensor exposed through HA) inject motion into
 * the kiosk when no camera is available — the primary motion fallback for Android TV boxes.
 * Each emission represents a remote "motion detected" pulse.
 *
 * @see ObserveMqttFabCommandUseCase
 * @since 1.2.0
 */
public interface ObserveMqttMotionCommandUseCase {
    /**
     * Returns a [Flow] that emits once for every `"ON"` command received on the device's
     * motion-set topic. The flow never completes under normal operation.
     *
     * @return A hot [Flow] of motion pulses.
     */
    public operator fun invoke(): Flow<Unit>
}
