package domain.usecase.impl.source.usecase.device

import domain.core.core.monad.Failure
import domain.core.source.model.MoveDetectorModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.device.SetMoveDetectorUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetMoveDetectorUseCase] using [ConfigureRepository].
 */
@Single(binds = [SetMoveDetectorUseCase::class])
internal class SetMoveDetectorUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : SetMoveDetectorUseCase {
    override suspend fun invoke(moveDetectorModel: MoveDetectorModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        configureRepository.setMoveDetector(moveDetectorModel)
    }
}
