package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the user's selected language and locale.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message LanguagePreference {
 *     optional string locale_code = 1;
 * }
 * ```
 *
 * @property localeCode the BCP 47 locale code (e.g., "en", "uk"), or `null` to use the system default.
 *
 * @see data.preferences.api.source.datasource.LanguagePreferenceSource
 *
 * @since 0.0.1
 */
public data class LanguagePreference(
    val localeCode: String?,
) : Resource
