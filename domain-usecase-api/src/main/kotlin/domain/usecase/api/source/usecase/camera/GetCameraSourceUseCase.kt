package domain.usecase.api.source.usecase.camera

import domain.core.source.model.CameraSourceModel

/**
 * Use case for retrieving the selected camera source.
 *
 * @see SetCameraSourceUseCase
 * @see ObserveCameraSourceUseCase
 * @see CameraSourceModel
 * @since 1.4.0
 */
public interface GetCameraSourceUseCase {

    /**
     * Reads the selected camera source from persistent storage.
     *
     * @return `Result.success` wrapping the current [CameraSourceModel] (defaulting to
     *   [CameraSourceModel.Auto] when unset), or `Result.failure` if the store is unavailable.
     */
    public suspend operator fun invoke(): Result<CameraSourceModel>
}
