package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the auto-return kiosk setting.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message AutoReturnProtoModel {
 *     bool is_enabled = 1;
 * }
 * ```
 *
 * @property isEnabled whether auto-return to kiosk is active after leaving to an external app.
 *
 * @see data.preferences.api.source.datasource.AutoReturnPreferenceSource
 *
 * @since 0.0.4
 */
public data class AutoReturnPreference(
    val isEnabled: Boolean? = null,
) : Resource
