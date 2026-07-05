package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.configuration.SetReduceMotionUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetReduceMotionUseCase] using [ConfigureRepository].
 *
 * @see SetReduceMotionUseCase
 * @since 0.0.6
 */
@Single(binds = [SetReduceMotionUseCase::class])
internal class SetReduceMotionUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetReduceMotionUseCase {
    override suspend fun invoke(value: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setReduceMotion(value)
    }
}
