package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.StreamingPreference

/**
 * Preference data source for camera streaming configuration.
 *
 * Manages persistence and observation of the MJPEG/camera stream settings,
 * including the enabled state, server port, quality, frame rate, and rotation.
 *
 * @see StreamingPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface StreamingPreferenceSource : PreferenceSource<StreamingPreference>
