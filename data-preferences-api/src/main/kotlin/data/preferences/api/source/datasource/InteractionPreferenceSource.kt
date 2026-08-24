package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.InteractionPreference

/**
 * Data source interface for the interaction preferences.
 *
 * @see InteractionPreference
 * @see PreferenceSource
 * @since 2.2.0
 */
public interface InteractionPreferenceSource : PreferenceSource<InteractionPreference>
