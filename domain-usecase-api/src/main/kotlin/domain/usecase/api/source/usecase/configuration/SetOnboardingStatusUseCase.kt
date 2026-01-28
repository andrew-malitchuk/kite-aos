package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.core.common.Optional

/**
 * Use case for updating the onboarding completion status.
 */
public interface SetOnboardingStatusUseCase {
    /**
     * @param value `true` if onboarding is finished, `false` otherwise.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(value: Boolean): Optional
}

