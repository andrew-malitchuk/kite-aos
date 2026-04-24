package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetOnboardingStatusUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetOnboardingStatusUseCase] using [ConfigureRepository].
 *
 * @see GetOnboardingStatusUseCase
 * @since 0.0.1
 */
@Single(binds = [GetOnboardingStatusUseCase::class])
internal class GetOnboardingStatusUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetOnboardingStatusUseCase {
    override suspend fun invoke(): Result<Boolean> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        // Extract isCompleted flag; throw NotFound if onboarding record is absent
        configureRepository.getOnboarding()?.isCompleted ?: throw Failure.Logic.NotFound
    }
}
