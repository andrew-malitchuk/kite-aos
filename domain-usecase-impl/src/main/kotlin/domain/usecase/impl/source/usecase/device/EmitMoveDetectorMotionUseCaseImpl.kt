package domain.usecase.impl.source.usecase.device

import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.device.EmitMoveDetectorMotionUseCase
import org.koin.core.annotation.Single

/**
 * Implementation of [EmitMoveDetectorMotionUseCase] using [ConfigureRepository].
 *
 * @see EmitMoveDetectorMotionUseCase
 * @since 0.0.1
 */
@Single(binds = [EmitMoveDetectorMotionUseCase::class])
internal class EmitMoveDetectorMotionUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : EmitMoveDetectorMotionUseCase {
    override suspend fun invoke() {
        configureRepository.emitMoveDetectorMotion()
    }
}
