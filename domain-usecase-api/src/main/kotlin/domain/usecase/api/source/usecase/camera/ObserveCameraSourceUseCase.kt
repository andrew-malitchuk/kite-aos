package domain.usecase.api.source.usecase.camera

import domain.core.source.model.CameraSourceModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time changes to the selected camera source.
 *
 * Emits the current [CameraSourceModel] on collection and re-emits on every subsequent change
 * persisted via [SetCameraSourceUseCase]. Emits `null` when no value has been saved yet.
 *
 * @see GetCameraSourceUseCase
 * @see SetCameraSourceUseCase
 * @see CameraSourceModel
 * @since 1.4.0
 */
public interface ObserveCameraSourceUseCase {

    /**
     * Returns a cold [Flow] backed by the preference data store.
     *
     * @return A [Flow] emitting the latest [CameraSourceModel], or `null` if unconfigured.
     */
    public operator fun invoke(): Flow<CameraSourceModel?>
}
