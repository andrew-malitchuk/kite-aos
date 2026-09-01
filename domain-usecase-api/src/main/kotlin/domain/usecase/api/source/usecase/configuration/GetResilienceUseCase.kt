package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ResilienceModel

/**
 * Use case for reading the unattended-operation safeguard settings.
 *
 * @see SetResilienceUseCase
 * @see ObserveResilienceUseCase
 * @since 2.2.0
 */
public interface GetResilienceUseCase {
    /**
     * Reads the persisted settings.
     *
     * @return `Result.success` wrapping the current [ResilienceModel] — defaults are substituted when
     *   nothing has been persisted yet, so callers always get a usable model — or
     *   `Result.failure` with a `Failure.Technical.Preference` when the store cannot be read.
     */
    public suspend operator fun invoke(): Result<ResilienceModel>
}
