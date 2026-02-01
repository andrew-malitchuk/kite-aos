package domain.usecase.impl.source.usecase.device

import domain.core.core.monad.Failure
import domain.core.source.model.MoveDetectorModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetMoveDetectorUseCase] using [ConfigureRepository].
 */
@Single(binds = [GetMoveDetectorUseCase::class])
internal class GetMoveDetectorUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : GetMoveDetectorUseCase {
    override suspend fun invoke(): Result<MoveDetectorModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        configureRepository.getMoveDetector() ?: throw Failure.Logic.NotFound
    }
}
