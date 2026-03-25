package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.MoveDetectorPreference
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for managing motion detector preferences.
 */
public interface MoveDetectorPreferenceSource : PreferenceSource<MoveDetectorPreference> {
    public fun observeMotion(): Flow<Unit>

    public suspend fun emitMotion()
}
