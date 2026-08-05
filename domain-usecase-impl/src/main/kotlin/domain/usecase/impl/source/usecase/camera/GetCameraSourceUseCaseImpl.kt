package domain.usecase.impl.source.usecase.camera

import domain.core.source.model.CameraSourceModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.camera.GetCameraSourceUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetCameraSourceUseCase] using [ConfigureRepository].
 *
 * Returns [CameraSourceModel.Auto] when no camera source has been persisted yet, so callers
 * always receive a valid value rather than null.
 *
 * @see GetCameraSourceUseCase
 * @since 1.4.0
 */
@Single(binds = [GetCameraSourceUseCase::class])
internal class GetCameraSourceUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetCameraSourceUseCase {
    override suspend fun invoke(): Result<CameraSourceModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getCameraSource() ?: CameraSourceModel.Auto
    }
}
