package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.ThemePreference

/**
 * Preference data source for application theme settings.
 *
 * Manages persistence and observation of the user's selected theme mode
 * (e.g., light, dark, or system default).
 *
 * @see ThemePreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface ThemePreferenceSource : PreferenceSource<ThemePreference>
