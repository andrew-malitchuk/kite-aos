package domain.usecase.impl.source.usecase.camera

import domain.core.source.model.CameraSourceModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.camera.ObserveCameraSourceUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveCameraSourceUseCase] using [ConfigureRepository].
 *
 * Emits `null` when no camera source is stored or when the stored value is cleared.
 *
 * @see ObserveCameraSourceUseCase
 * @since 1.4.0
 */
@Single(binds = [ObserveCameraSourceUseCase::class])
internal class ObserveCameraSourceUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveCameraSourceUseCase {
    override fun invoke(): Flow<CameraSourceModel?> = configureRepository.observeCameraSource()
}
