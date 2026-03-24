package domain.usecase.impl.source.usecase.application

import domain.core.core.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.repository.api.source.repository.ApplicationRepository
import domain.usecase.api.source.usecase.application.GetApplicationsUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetApplicationsUseCase] using [ApplicationRepository].
 */
@Single(binds = [GetApplicationsUseCase::class])
internal class GetApplicationsUseCaseImpl(
    private val applicationRepository: ApplicationRepository,
) : GetApplicationsUseCase {
    override suspend fun invoke(): Result<List<ApplicationModel>> = resultLauncher(
        errorMapper = Failure.Technical::Database,
    ) {
        applicationRepository.getApplications()
    }
}
