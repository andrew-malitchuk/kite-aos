package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.usecase.api.source.common.Optional
import domain.core.source.model.WebViewRefreshModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.SetWebViewRefreshUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetWebViewRefreshUseCase] using [ConfigureRepository].
 *
 * @see SetWebViewRefreshUseCase
 * @since 0.0.6
 */
@Single(binds = [SetWebViewRefreshUseCase::class])
internal class SetWebViewRefreshUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetWebViewRefreshUseCase {
    override suspend fun invoke(model: WebViewRefreshModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setWebViewRefresh(model)
    }
}
