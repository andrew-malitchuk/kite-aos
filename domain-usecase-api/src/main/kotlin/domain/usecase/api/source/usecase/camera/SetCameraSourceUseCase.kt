package domain.usecase.api.source.usecase.camera

import domain.core.source.model.CameraSourceModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the selected camera source.
 *
 * @see GetCameraSourceUseCase
 * @see ObserveCameraSourceUseCase
 * @see CameraSourceModel
 * @since 1.4.0
 */
public interface SetCameraSourceUseCase {

    /**
     * Writes the given camera source to persistent storage.
     *
     * @param camera The camera source to persist.
     * @return `Result.success(Unit)` on success, or `Result.failure` if the write fails.
     */
    public suspend operator fun invoke(camera: CameraSourceModel): Optional
}
