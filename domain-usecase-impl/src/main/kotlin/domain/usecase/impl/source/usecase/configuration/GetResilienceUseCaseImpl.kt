package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.ResilienceModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetResilienceUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetResilienceUseCase] using [ConfigureRepository].
 *
 * Substitutes a default [ResilienceModel] when nothing has been persisted yet, so callers never have
 * to distinguish "not configured" from "configured to the defaults".
 *
 * @see GetResilienceUseCase
 * @since 2.2.0
 */
@Single(binds = [GetResilienceUseCase::class])
internal class GetResilienceUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetResilienceUseCase {
    override suspend operator fun invoke(): Result<ResilienceModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getResilience() ?: ResilienceModel()
    }
}
