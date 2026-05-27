package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.AutoReturnPreference

/**
 * Data source contract for auto-return preference storage.
 *
 * @see AutoReturnPreference
 * @see PreferenceSource
 * @since 0.0.4
 */
public interface AutoReturnPreferenceSource : PreferenceSource<AutoReturnPreference>
