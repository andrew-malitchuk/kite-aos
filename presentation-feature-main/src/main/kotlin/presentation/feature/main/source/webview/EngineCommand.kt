package presentation.feature.main.source.webview

import androidx.compose.runtime.Immutable

/**
 * A one-shot instruction for the mounted kiosk engine, passed from `MainScreen` into `MainContent`.
 *
 * The engine lives inside the composition, while the things that drive it — MQTT commands, the
 * connection monitor, the inactivity timer — reach the screen as Orbit side effects. This type is
 * the seam between the two.
 *
 * [id] exists because the commands are events, not state: pressing "reload" twice, or navigating
 * to the URL already loaded, must act twice. A monotonically increasing id makes each dispatch a
 * distinct value, so the `LaunchedEffect` keyed on it restarts even when [action] and [value]
 * repeat.
 *
 * @property id Monotonic dispatch counter; makes otherwise identical commands distinct.
 * @property action What the engine should do.
 * @property value Payload for the actions that need one; empty for the rest.
 * @since 2.2.0
 */
@Immutable
public data class EngineCommand(
    val id: Int,
    val action: Action,
    val value: String = "",
) {
    /**
     * The engine operations reachable through [EngineCommand].
     *
     * @since 2.2.0
     */
    public enum class Action {
        /** Re-request the current page, preserving its path. */
        RELOAD,

        /** Drop cached assets and reload, preserving the Home Assistant session. */
        CLEAR_CACHE,

        /** Suspend page execution while the dashboard backend is unreachable. */
        PAUSE,

        /** Resume page execution after [PAUSE]. */
        RESUME,

        /** Load the configured home URL, discarding the current path. */
        NAVIGATE_HOME,

        /** Load the URL carried in [value]. */
        NAVIGATE,

        /** Step one entry back in history. */
        BACK,

        /** Step one entry forward in history. */
        FORWARD,

        /** Run the script carried in [value] inside the loaded page. */
        EVALUATE_JS,
    }
}
