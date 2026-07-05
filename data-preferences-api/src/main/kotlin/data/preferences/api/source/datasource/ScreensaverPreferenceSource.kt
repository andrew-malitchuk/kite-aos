package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.ScreensaverPreference

/**
 * Preference data source for screensaver configuration.
 *
 * Manages persistence and observation of screensaver settings, including the enabled state,
 * activation delay, slide interval, clock visibility, media source, and local folder URI.
 *
 * @see ScreensaverPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface ScreensaverPreferenceSource : PreferenceSource<ScreensaverPreference>
