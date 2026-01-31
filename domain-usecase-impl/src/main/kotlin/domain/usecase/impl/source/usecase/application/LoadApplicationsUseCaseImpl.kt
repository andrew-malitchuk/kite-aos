package domain.usecase.impl.source.usecase.application

import domain.core.core.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.repository.api.source.repository.ApplicationRepository
import domain.usecase.api.source.usecase.application.LoadApplicationsUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [LoadApplicationsUseCase].
 *
 * This use case combines data from system-installed applications and the local database
 * to provide a complete view of which applications are available and which have been
 * selected (chosen) by the user.
 */
@Single(binds = [LoadApplicationsUseCase::class])
internal class LoadApplicationsUseCaseImpl(
    private val applicationRepository: ApplicationRepository
) : LoadApplicationsUseCase {
    override suspend fun invoke(chosen: Boolean): Result<List<ApplicationModel>> = resultLauncher(
        errorMapper = Failure.Technical::Database
    ) {
        val installedApps = applicationRepository.getApplications()
        val savedApps = applicationRepository.loadApplications()

        val apps = installedApps.map { installedApp ->
            val savedApp = savedApps.firstOrNull {
                it.packageName == installedApp.packageName
            }

            installedApp.copy(
                id = savedApp?.id,
                chosen = savedApp != null
            )
        }

        if (chosen) {
            apps.filter { it.chosen == true }
        } else {
            apps
        }
    }
}