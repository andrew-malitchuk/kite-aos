package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.ResilienceModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.configuration.SetResilienceUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetResilienceUseCase] using [ConfigureRepository].
 *
 * @see SetResilienceUseCase
 * @since 2.2.0
 */
@Single(binds = [SetResilienceUseCase::class])
internal class SetResilienceUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetResilienceUseCase {
    override suspend operator fun invoke(resilience: ResilienceModel?): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setResilience(resilience)
    }
}
