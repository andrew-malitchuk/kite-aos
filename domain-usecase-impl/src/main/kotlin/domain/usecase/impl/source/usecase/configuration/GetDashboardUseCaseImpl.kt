package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.core.source.model.DashboardModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetDashboardUseCase] using [ConfigureRepository].
 */
@Single(binds = [GetDashboardUseCase::class])
internal class GetDashboardUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : GetDashboardUseCase {
    override suspend fun invoke(): Result<DashboardModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        configureRepository.getDashboard() ?: throw Failure.Logic.NotFound
    }
}
