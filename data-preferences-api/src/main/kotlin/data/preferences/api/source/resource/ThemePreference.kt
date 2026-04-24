package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the application theme setting.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message ThemePreference {
 *     string mode = 1;
 * }
 * ```
 *
 * @property mode the theme mode identifier (e.g., "light", "dark", "system").
 *
 * @see data.preferences.api.source.datasource.ThemePreferenceSource
 *
 * @since 0.0.1
 */
public data class ThemePreference(
    val mode: String,
) : Resource
