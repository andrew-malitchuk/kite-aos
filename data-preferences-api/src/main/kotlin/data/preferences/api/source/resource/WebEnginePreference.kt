package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the selected browser engine setting.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message WebEnginePreference {
 *     string engine = 1;
 * }
 * ```
 *
 * @property engine the engine identifier (e.g., "android_webview", "geckoview").
 *
 * @see data.preferences.api.source.datasource.WebEnginePreferenceSource
 *
 * @since 0.0.4
 */
public data class WebEnginePreference(
    val engine: String,
) : Resource
