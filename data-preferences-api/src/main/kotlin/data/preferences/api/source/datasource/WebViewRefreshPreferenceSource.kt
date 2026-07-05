package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.WebViewRefreshPreference

/**
 * Data source interface for managing periodic WebView refresh preferences.
 *
 * @see WebViewRefreshPreference
 * @see PreferenceSource
 * @since 0.0.6
 */
public interface WebViewRefreshPreferenceSource : PreferenceSource<WebViewRefreshPreference>
