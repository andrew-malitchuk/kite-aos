package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.AutoRebootPreference

/**
 * Preference data source for automatic device reboot scheduling.
 *
 * Manages persistence and observation of the scheduled reboot configuration,
 * including the enabled state, scheduled time (hour/minute), and recurrence interval.
 *
 * @see AutoRebootPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface AutoRebootPreferenceSource : PreferenceSource<AutoRebootPreference>
