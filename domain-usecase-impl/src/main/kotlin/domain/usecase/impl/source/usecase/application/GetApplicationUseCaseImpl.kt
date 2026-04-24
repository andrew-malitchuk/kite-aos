package domain.usecase.impl.source.usecase.application

import domain.core.core.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.repository.api.source.repository.ApplicationRepository
import domain.usecase.api.source.usecase.application.GetApplicationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetApplicationUseCase] using [ApplicationRepository].
 *
 * @property applicationRepository The repository to retrieve application info from.
 *
 * @see GetApplicationUseCase
 * @since 0.0.1
 */
@Single(binds = [GetApplicationUseCase::class])
internal class GetApplicationUseCaseImpl(
    private val applicationRepository: ApplicationRepository,
) : GetApplicationUseCase {
    override suspend fun invoke(packageName: String): Result<ApplicationModel> = resultLauncher(
        errorMapper = Failure.Technical::Database,
    ) {
        applicationRepository.getApplication(packageName) ?: throw Failure.Technical.Platform(
            Failure.Logic.NotFound,
        )
    }
}
