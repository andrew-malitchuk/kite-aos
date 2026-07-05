package domain.usecase.api.source.usecase.device

import domain.core.source.model.ScreenStateModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for publishing a screen power-state change to all active observers.
 *
 * Called by platform services (e.g., `MotionService`) when the screen turns on or off,
 * allowing other components to react without coupling to Android broadcast receivers.
 *
 * @see ObserveScreenStateUseCase
 * @since 0.0.2
 */
public interface EmitScreenStateUseCase {

    /**
     * Broadcasts the given [state] to the shared screen-state channel.
     *
     * @param state The new screen power state to publish.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the underlying channel is unavailable.
     */
    public suspend operator fun invoke(state: ScreenStateModel): Optional
}
