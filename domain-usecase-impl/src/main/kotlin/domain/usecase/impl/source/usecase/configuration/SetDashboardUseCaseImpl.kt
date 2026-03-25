package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.core.source.model.DashboardModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.configuration.SetDashboardUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetDashboardUseCase] using [ConfigureRepository].
 */
@Single(binds = [SetDashboardUseCase::class])
internal class SetDashboardUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetDashboardUseCase {
    override suspend fun invoke(dashboardModel: DashboardModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setDashboard(dashboardModel)
    }
}
