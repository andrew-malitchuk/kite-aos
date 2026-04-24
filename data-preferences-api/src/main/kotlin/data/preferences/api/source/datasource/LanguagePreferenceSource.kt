package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.LanguagePreference

/**
 * Preference data source for language and locale settings.
 *
 * Manages persistence and observation of the user's selected locale preference.
 *
 * @see LanguagePreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface LanguagePreferenceSource : PreferenceSource<LanguagePreference>
