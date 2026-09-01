package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.InteractionModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the interaction settings (inactivity reset, volume gesture).
 *
 * @see GetInteractionUseCase
 * @since 2.2.0
 */
public interface SetInteractionUseCase {
    /**
     * Persists the given settings.
     *
     * @param interaction the settings to store, or `null` to reset to defaults.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a
     *   `Failure.Technical.Preference` when the write fails.
     */
    public suspend operator fun invoke(interaction: InteractionModel?): Optional
}
