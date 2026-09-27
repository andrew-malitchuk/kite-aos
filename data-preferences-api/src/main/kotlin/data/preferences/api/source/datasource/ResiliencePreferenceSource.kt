package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.ResiliencePreference

/**
 * Data source interface for the unattended-operation safeguard preferences.
 *
 * @see ResiliencePreference
 * @see PreferenceSource
 * @since 2.2.0
 */
public interface ResiliencePreferenceSource : PreferenceSource<ResiliencePreference>
