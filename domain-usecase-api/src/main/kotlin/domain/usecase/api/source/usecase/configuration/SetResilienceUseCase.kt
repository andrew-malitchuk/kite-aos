package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ResilienceModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the unattended-operation safeguard settings.
 *
 * @see GetResilienceUseCase
 * @since 2.2.0
 */
public interface SetResilienceUseCase {
    /**
     * Persists the given settings.
     *
     * @param resilience the settings to store, or `null` to reset to defaults.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a
     *   `Failure.Technical.Preference` when the write fails.
     */
    public suspend operator fun invoke(resilience: ResilienceModel?): Optional
}
