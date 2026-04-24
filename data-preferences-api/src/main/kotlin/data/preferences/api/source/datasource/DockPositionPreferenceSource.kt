package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.DockPreference

/**
 * Preference data source for dock position settings.
 *
 * Manages persistence and observation of the dock placement preference on the home screen.
 *
 * @see DockPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface DockPositionPreferenceSource : PreferenceSource<DockPreference>
