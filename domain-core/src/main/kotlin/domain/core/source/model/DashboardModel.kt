package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for the kiosk dashboard configuration.
 *
 * @property dashboardUrl The main URL to be displayed in the kiosk WebView.
 * @property whitelistUrl A pattern or URL used to restrict navigation within the WebView.
 * @property trustAllSsl When `true`, the WebView accepts all SSL certificates including self-signed
 *   ones. Use with caution — only enable for trusted local network Home Assistant instances.
 *
 * @see Model
 * @since 0.0.1
 */
public data class DashboardModel(
    val dashboardUrl: String,
    val whitelistUrl: String,
    val trustAllSsl: Boolean = false,
) : Model
