package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.core.source.model.WebEngineModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.configuration.SetWebEngineUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetWebEngineUseCase] using [ConfigureRepository].
 *
 * @see SetWebEngineUseCase
 * @since 0.0.4
 */
@Single(binds = [SetWebEngineUseCase::class])
internal class SetWebEngineUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetWebEngineUseCase {
    override suspend fun invoke(value: WebEngineModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setWebEngine(value)
    }
}
