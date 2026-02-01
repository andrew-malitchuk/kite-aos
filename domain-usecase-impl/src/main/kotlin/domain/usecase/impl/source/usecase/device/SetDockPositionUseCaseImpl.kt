package domain.usecase.impl.source.usecase.device

import domain.core.core.monad.Failure
import domain.core.source.model.DockPositionModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.device.SetDockPositionUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetDockPositionUseCase] using [ConfigureRepository].
 */
@Single(binds = [SetDockPositionUseCase::class])
internal class SetDockPositionUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : SetDockPositionUseCase {
    override suspend fun invoke(dockPositionModel: DockPositionModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        configureRepository.setDockPosition(dockPositionModel)
    }
}
