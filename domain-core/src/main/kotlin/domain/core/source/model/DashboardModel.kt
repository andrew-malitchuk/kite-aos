package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for the kiosk dashboard configuration.
 *
 * @property dashboardUrl The main URL to be displayed in the kiosk WebView.
 * @property whitelistUrl A pattern or URL used to restrict navigation within the WebView.
 *
 * @see Model
 * @since 0.0.1
 */
public data class DashboardModel(
    val dashboardUrl: String,
    val whitelistUrl: String,
) : Model
