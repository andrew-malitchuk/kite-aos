package domain.usecase.api.source.usecase.configuration

/**
 * Use case for checking if the initial onboarding flow has been completed.
 *
 * @see SetOnboardingStatusUseCase
 * @since 0.0.1
 */
public interface GetOnboardingStatusUseCase {
    /**
     * Reads the onboarding completion flag from persistent storage.
     *
     * @return `Result.success(true)` if the user has completed onboarding, `Result.success(false)`
     *   if it has not been completed yet, or `Result.failure` with a `Failure` if the
     *   preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<Boolean>
}
