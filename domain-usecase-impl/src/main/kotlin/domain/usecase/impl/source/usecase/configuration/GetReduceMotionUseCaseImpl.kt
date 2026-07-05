package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetReduceMotionUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetReduceMotionUseCase] using [ConfigureRepository].
 *
 * @see GetReduceMotionUseCase
 * @since 0.0.6
 */
@Single(binds = [GetReduceMotionUseCase::class])
internal class GetReduceMotionUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetReduceMotionUseCase {
    override suspend fun invoke(): Result<Boolean> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getReduceMotion() ?: false
    }
}
