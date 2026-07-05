package domain.usecase.api.source.usecase.device

import domain.core.source.model.ScreenStateModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time screen power-state changes.
 *
 * Consumers (e.g., MQTT service) collect this flow to react to screen-on/off transitions
 * without depending on Android system broadcasts.
 *
 * @see EmitScreenStateUseCase
 * @since 0.0.2
 */
public interface ObserveScreenStateUseCase {

    /**
     * Returns a [Flow] that emits every [ScreenStateModel] published via [EmitScreenStateUseCase].
     *
     * The flow does not emit an initial value; it only emits on subsequent state changes.
     * It never completes under normal operation — it terminates only when the collector's
     * coroutine scope is cancelled.
     *
     * @return A [Flow] of [ScreenStateModel] representing screen power-state transitions.
     */
    public operator fun invoke(): Flow<ScreenStateModel>
}
