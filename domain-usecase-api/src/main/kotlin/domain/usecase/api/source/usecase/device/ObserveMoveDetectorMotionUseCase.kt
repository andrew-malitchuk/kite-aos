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
     * @return A [Flow] that emits [Unit] whenever motion is detected.
     */
    public operator fun invoke(): Flow<Unit>
}
