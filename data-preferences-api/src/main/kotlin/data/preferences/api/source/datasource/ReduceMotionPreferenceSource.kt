package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.ReduceMotionPreference

/**
 * Data source contract for reduce motion preference storage.
 *
 * @see ReduceMotionPreference
 * @see PreferenceSource
 * @since 0.0.6
 */
public interface ReduceMotionPreferenceSource : PreferenceSource<ReduceMotionPreference>
