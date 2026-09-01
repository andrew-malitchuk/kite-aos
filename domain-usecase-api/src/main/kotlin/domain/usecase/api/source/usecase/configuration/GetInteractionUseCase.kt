package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.InteractionModel

/**
 * Use case for reading the interaction settings (inactivity reset, volume gesture).
 *
 * @see SetInteractionUseCase
 * @see ObserveInteractionUseCase
 * @since 2.2.0
 */
public interface GetInteractionUseCase {
    /**
     * Reads the persisted settings.
     *
     * @return `Result.success` wrapping the current [InteractionModel] — defaults are substituted when
     *   nothing has been persisted yet, so callers always get a usable model — or
     *   `Result.failure` with a `Failure.Technical.Preference` when the store cannot be read.
     */
    public suspend operator fun invoke(): Result<InteractionModel>
}
