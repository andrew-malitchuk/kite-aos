package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing dashboard configuration.
 *
 * Stores the primary dashboard URL and an optional whitelist URL used to restrict
 * navigation within the kiosk web view.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message DashboardPreference {
 *     optional string dashboard_url = 1;
 *     optional string whitelist_url = 2;
 * }
 * ```
 *
 * @property dashboardUrl the URL of the Home Assistant dashboard to display, or `null` if not configured.
 * @property whitelistUrl the URL pattern used to restrict allowed navigation, or `null` if unrestricted.
 *
 * @see data.preferences.api.source.datasource.DashboardPreferenceSource
 *
 * @since 0.0.1
 */
public data class DashboardPreference(
    val dashboardUrl: String? = null,
    val whitelistUrl: String? = null,
) : Resource
