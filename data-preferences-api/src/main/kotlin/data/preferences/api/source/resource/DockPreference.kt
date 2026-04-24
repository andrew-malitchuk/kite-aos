package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the dock position on the home screen.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message DockPreference {
 *     string position = 1;
 * }
 * ```
 *
 * @property position the dock placement identifier (e.g., "bottom", "left", "right").
 *
 * @see data.preferences.api.source.datasource.DockPositionPreferenceSource
 *
 * @since 0.0.1
 */
public data class DockPreference(
    val position: String,
) : Resource
