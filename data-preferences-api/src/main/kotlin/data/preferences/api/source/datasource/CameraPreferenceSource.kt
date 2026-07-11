package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.CameraPreference

/**
 * Preference data source for the selected camera source.
 *
 * Manages persistence and observation of the user's chosen camera (auto / front / rear /
 * external) used by the motion-detection and streaming pipeline.
 *
 * @see CameraPreference
 * @see PreferenceSource
 *
 * @since 1.4.0
 */
public interface CameraPreferenceSource : PreferenceSource<CameraPreference>
