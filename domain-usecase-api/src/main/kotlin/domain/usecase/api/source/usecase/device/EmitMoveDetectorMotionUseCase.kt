package domain.usecase.api.source.usecase.device

import domain.usecase.api.source.common.Optional

/**
 * Use case for manually triggering or reporting a motion detection event.
 *
 * @see ObserveMoveDetectorMotionUseCase
 * @since 0.0.1
 */
public interface EmitMoveDetectorMotionUseCase {
    /**
     * Broadcasts a motion detection event to all active [ObserveMoveDetectorMotionUseCase] collectors.
     *
     * Called by `MotionService` each time the CameraX luma analyser crosses the configured
     * motion threshold. Observers receive a `Unit` emission and are responsible for
     * updating screen state and MQTT telemetry.
     *
     * @return `Result.success(Unit)` on successful emission, or `Result.failure` with a
     *   `Failure` if the underlying shared-flow channel is unavailable.
     */
    public suspend operator fun invoke(): Optional
}
