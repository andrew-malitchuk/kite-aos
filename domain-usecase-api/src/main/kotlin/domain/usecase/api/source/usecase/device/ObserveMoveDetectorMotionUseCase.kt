package domain.usecase.api.source.usecase.device

import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time motion detection events.
 *
 * @see EmitMoveDetectorMotionUseCase
 * @since 0.0.1
 */
public interface ObserveMoveDetectorMotionUseCase {
    /**
     * Returns a [Flow] that emits [Unit] each time a motion event is published via
     * [EmitMoveDetectorMotionUseCase].
     *
     * The flow does not replay the most recent event on new collectors and never completes
     * under normal operation — it terminates only when the collector's scope is cancelled.
     *
     * @return A hot [Flow] of [Unit], where each emission represents a single motion detection event.
     */
    public operator fun invoke(): Flow<Unit>
}
