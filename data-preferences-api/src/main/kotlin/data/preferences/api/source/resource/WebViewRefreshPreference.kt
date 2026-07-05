package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing periodic WebView refresh configuration.
 *
 * @property enabled whether automatic periodic reloading of the kiosk WebView is enabled.
 * @property intervalSeconds the interval in seconds between automatic reloads.
 *
 * @see data.preferences.api.source.datasource.WebViewRefreshPreferenceSource
 * @since 0.0.6
 */
public data class WebViewRefreshPreference(
    val enabled: Boolean? = null,
    val intervalSeconds: Long? = null,
) : Resource
