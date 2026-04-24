package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.MoveDetectorPreference
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for managing motion detector preferences.
 *
 * Extends [PreferenceSource] with additional motion event signaling capabilities.
 * In addition to standard preference CRUD operations, this source provides a channel
 * for emitting and observing raw motion detection events.
 *
 * @see MoveDetectorPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface MoveDetectorPreferenceSource : PreferenceSource<MoveDetectorPreference> {

    /**
     * Observes motion detection events as a reactive stream.
     *
     * Each emission of [Unit] indicates that motion was detected by the camera-based
     * presence detector.
     *
     * @return a [Flow] that emits [Unit] each time motion is detected.
     */
    public fun observeMotion(): Flow<Unit>

    /**
     * Emits a single motion detection event into the motion observation stream.
     *
     * This should be called by the motion detection service whenever motion is detected.
     */
    public suspend fun emitMotion()
}
