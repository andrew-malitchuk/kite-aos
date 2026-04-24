package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.core.common.Optional

/**
 * Use case for updating the onboarding completion status.
 *
 * @see GetOnboardingStatusUseCase
 * @since 0.0.1
 */
public interface SetOnboardingStatusUseCase {
    /**
     * @param value `true` if onboarding is finished, `false` otherwise.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(value: Boolean): Optional
}
