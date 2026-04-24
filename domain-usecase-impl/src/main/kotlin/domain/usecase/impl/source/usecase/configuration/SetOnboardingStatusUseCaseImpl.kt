package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.core.source.model.OnboardingModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.configuration.SetOnboardingStatusUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetOnboardingStatusUseCase] using [ConfigureRepository].
 *
 * @see SetOnboardingStatusUseCase
 * @since 0.0.1
 */
@Single(binds = [SetOnboardingStatusUseCase::class])
internal class SetOnboardingStatusUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetOnboardingStatusUseCase {
    override suspend fun invoke(value: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        // Wrap the raw boolean into an OnboardingModel before persisting
        val onboardingModel = OnboardingModel(isCompleted = value)
        configureRepository.setOnboarding(onboardingModel)
    }
}
