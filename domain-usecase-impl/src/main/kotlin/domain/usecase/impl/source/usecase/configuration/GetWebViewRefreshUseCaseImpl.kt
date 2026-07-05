package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.core.source.monad.Failure.Logic.NotFound
import domain.core.source.model.WebViewRefreshModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetWebViewRefreshUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetWebViewRefreshUseCase] using [ConfigureRepository].
 *
 * @see GetWebViewRefreshUseCase
 * @since 0.0.6
 */
@Single(binds = [GetWebViewRefreshUseCase::class])
internal class GetWebViewRefreshUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetWebViewRefreshUseCase {
    override suspend fun invoke(): Result<WebViewRefreshModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getWebViewRefresh() ?: throw NotFound
    }
}
