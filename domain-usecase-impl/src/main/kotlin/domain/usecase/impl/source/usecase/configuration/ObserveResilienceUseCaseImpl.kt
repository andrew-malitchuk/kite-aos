package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.ResilienceModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.ObserveResilienceUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveResilienceUseCase] using [ConfigureRepository].
 *
 * @see ObserveResilienceUseCase
 * @since 2.2.0
 */
@Single(binds = [ObserveResilienceUseCase::class])
internal class ObserveResilienceUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveResilienceUseCase {
    override operator fun invoke(): Flow<ResilienceModel?> = configureRepository.observeResilience()
}
