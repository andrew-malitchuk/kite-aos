package domain.usecase.api.source.usecase.device

/**
 * Use case for manually triggering or reporting a motion detection event.
 */
public interface EmitMoveDetectorMotionUseCase {
    /**
     * Emits a motion event to observers.
     */
    public suspend operator fun invoke()
}
