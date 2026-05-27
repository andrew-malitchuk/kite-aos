package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.core.source.model.WebEngineModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetWebEngineUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetWebEngineUseCase] using [ConfigureRepository].
 *
 * @see GetWebEngineUseCase
 * @since 0.0.4
 */
@Single(binds = [GetWebEngineUseCase::class])
internal class GetWebEngineUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetWebEngineUseCase {
    override suspend fun invoke(): Result<WebEngineModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getWebEngine() ?: WebEngineModel.AndroidWebView
    }
}
