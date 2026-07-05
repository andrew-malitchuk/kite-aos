package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for the periodic WebView refresh settings.
 *
 * @property enabled Whether automatic periodic reloading of the kiosk WebView is enabled.
 * @property intervalSeconds The interval in seconds between automatic reloads.
 *
 * @see Model
 * @since 0.0.6
 */
public data class WebViewRefreshModel(
    val enabled: Boolean?,
    val intervalSeconds: Long?,
) : Model
