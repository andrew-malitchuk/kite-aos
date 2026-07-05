package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional

/**
 * Use case for updating the onboarding completion status.
 *
 * @see GetOnboardingStatusUseCase
 * @since 0.0.1
 */
public interface SetOnboardingStatusUseCase {
    /**
     * Writes the onboarding completion flag to persistent storage.
     *
     * @param value `true` to mark onboarding as finished, `false` to mark it as pending.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(value: Boolean): Optional
}
