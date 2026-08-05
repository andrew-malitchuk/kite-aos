package domain.usecase.impl.source.usecase.camera

import domain.core.source.model.CameraSourceModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.camera.SetCameraSourceUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetCameraSourceUseCase] using [ConfigureRepository].
 *
 * Persists the selected camera source; failures are mapped to
 * [domain.core.source.monad.Failure.Technical.Preference].
 *
 * @see SetCameraSourceUseCase
 * @since 1.4.0
 */
@Single(binds = [SetCameraSourceUseCase::class])
internal class SetCameraSourceUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetCameraSourceUseCase {
    override suspend fun invoke(camera: CameraSourceModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setCameraSource(camera)
    }
}
