package presentation.core.platform.source.connection

/**
 * States of the dashboard backend connection.
 *
 * The intermediate [Suspect] state is not cosmetic: without a grace period, a one-second network
 * hiccup would pause and resume the WebView, which is itself disruptive and would flap constantly
 * on a marginal Wi-Fi link.
 *
 * @since 2.2.0
 */
public enum class DashboardConnectionState {
    /** The backend is reachable and the WebView is running normally. */
    HEALTHY,

    /** An error was seen; waiting out the grace period before acting on it. */
    SUSPECT,

    /** The backend is confirmed down and the WebView has been paused to stop its retry storm. */
    PAUSED,
}
