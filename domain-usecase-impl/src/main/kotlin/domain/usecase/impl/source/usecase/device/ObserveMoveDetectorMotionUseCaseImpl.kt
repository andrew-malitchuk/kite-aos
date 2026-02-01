package domain.usecase.impl.source.usecase.device

import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.device.ObserveMoveDetectorMotionUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveMoveDetectorMotionUseCase] using [ConfigureRepository].
 */
@Single(binds = [ObserveMoveDetectorMotionUseCase::class])
internal class ObserveMoveDetectorMotionUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : ObserveMoveDetectorMotionUseCase {
    override fun invoke(): Flow<Unit> =
        configureRepository.observeMoveDetectorMotion()
}
