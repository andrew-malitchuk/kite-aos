package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.WebEnginePreference

/**
 * Data source contract for browser engine preference storage.
 *
 * @see WebEnginePreference
 * @see PreferenceSource
 * @since 0.0.4
 */
public interface WebEnginePreferenceSource : PreferenceSource<WebEnginePreference>
