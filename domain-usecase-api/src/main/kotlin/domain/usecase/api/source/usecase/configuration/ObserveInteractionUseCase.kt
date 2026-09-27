package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.InteractionModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing the interaction settings (inactivity reset, volume gesture).
 *
 * @see GetInteractionUseCase
 * @since 2.2.0
 */
public interface ObserveInteractionUseCase {
    /**
     * Returns a [Flow] emitting the settings on every change.
     *
     * @return a hot [Flow] of the current [InteractionModel].
     */
    public operator fun invoke(): Flow<InteractionModel?>
}
