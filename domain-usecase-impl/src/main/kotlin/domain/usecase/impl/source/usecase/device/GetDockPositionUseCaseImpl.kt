package domain.usecase.impl.source.usecase.device

import domain.core.core.monad.Failure
import domain.core.source.model.DockPositionModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.device.GetDockPositionUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetDockPositionUseCase] using [ConfigureRepository].
 *
 * @see GetDockPositionUseCase
 * @since 0.0.1
 */
@Single(binds = [GetDockPositionUseCase::class])
internal class GetDockPositionUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetDockPositionUseCase {
    override suspend fun invoke(): Result<DockPositionModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        // Throw NotFound if no dock position has been configured yet
        configureRepository.getDockPosition() ?: throw Failure.Logic.NotFound
    }
}
