package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.DashboardPreference

/**
 * Preference data source for dashboard configuration settings.
 *
 * Manages persistence and observation of the dashboard URL and whitelist URL preferences
 * used by the kiosk web view.
 *
 * @see DashboardPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface DashboardPreferenceSource : PreferenceSource<DashboardPreference>
