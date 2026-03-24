package domain.usecase.api.source.usecase.configuration

/**
 * Use case for checking if the initial onboarding flow has been completed.
 */
public interface GetOnboardingStatusUseCase {
    /**
     * @return A [Result] containing `true` if onboarding is completed, `false` otherwise.
     */
    public suspend operator fun invoke(): Result<Boolean>
}
