package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing the browser engine used to render the kiosk dashboard.
 *
 * @property id The string identifier for the engine.
 *
 * @see Model
 * @since 0.0.4
 */
public enum class WebEngineModel(public val id: String) : Model {
    /** Android system WebView (default, lower APK overhead). */
    AndroidWebView("android_webview"),

    /** Mozilla GeckoView (Firefox engine, better WebRTC/media support). */
    GeckoView("geckoview"),
}
