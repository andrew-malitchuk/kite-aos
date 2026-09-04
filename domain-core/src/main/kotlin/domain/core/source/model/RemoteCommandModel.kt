package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for an inbound remote command that drives the kiosk WebView.
 *
 * Commands arrive on the MQTT command topic (`{clientId}/command/set`) as a JSON object
 * `{ "action": "...", "value": "..." }` and are decoded into this type before they reach the
 * presentation layer. Keeping the vocabulary here rather than at the transport means a second
 * transport — a local HTTP control API, say — decodes into the same commands and inherits the
 * behaviour instead of reimplementing it.
 *
 * [action] is the wire name and must stay stable: it is what users type into their Home Assistant
 * automations, so renaming one silently breaks every install that uses it.
 *
 * @property action Stable wire identifier for this command.
 * @see Model
 * @since 2.2.0
 */
public sealed class RemoteCommandModel(public val action: String) : Model {

    /**
     * Loads an arbitrary [url] in the kiosk WebView.
     *
     * Deliberately not filtered through the navigation whitelist — the whitelist exists to stop a
     * passer-by wandering off the dashboard, and a command published to the broker is by
     * definition not a passer-by.
     */
    public data class Navigate(val url: String) : RemoteCommandModel(ACTION_NAVIGATE)

    /** Re-requests the current page, preserving whatever path the dashboard is on. */
    public data object Reload : RemoteCommandModel(ACTION_RELOAD)

    /** Steps one entry back in the WebView history. */
    public data object Back : RemoteCommandModel(ACTION_BACK)

    /** Steps one entry forward in the WebView history. */
    public data object Forward : RemoteCommandModel(ACTION_FORWARD)

    /** Drops cached page assets and reloads, preserving cookies and the Home Assistant session. */
    public data object ClearCache : RemoteCommandModel(ACTION_CLEAR_CACHE)

    /** Returns to the configured home URL, discarding the current path. */
    public data object NavigateHome : RemoteCommandModel(ACTION_NAVIGATE_HOME)

    /**
     * Executes [script] inside the currently loaded page.
     *
     * Supported on the Android WebView engine only; GeckoView exposes no equivalent entry point
     * and ignores the command.
     */
    public data class EvaluateJs(val script: String) : RemoteCommandModel(ACTION_EVALUATE_JS)

    public companion object {
        /** Wire name for [Navigate]. */
        public const val ACTION_NAVIGATE: String = "navigate"

        /** Wire name for [Reload]. */
        public const val ACTION_RELOAD: String = "reload"

        /** Wire name for [Back]. */
        public const val ACTION_BACK: String = "back"

        /** Wire name for [Forward]. */
        public const val ACTION_FORWARD: String = "forward"

        /** Wire name for [ClearCache]. */
        public const val ACTION_CLEAR_CACHE: String = "clear_cache"

        /** Wire name for [NavigateHome]. */
        public const val ACTION_NAVIGATE_HOME: String = "navigate_home"

        /** Wire name for [EvaluateJs]. */
        public const val ACTION_EVALUATE_JS: String = "evaluate_js"

        /**
         * Builds the command named by [action], or `null` when it is unknown or unusable.
         *
         * Returning `null` rather than throwing is the point: a malformed publish from a
         * hand-written automation must be ignorable, not something that tears down the collector
         * and takes the whole command channel with it.
         *
         * @param action The wire name from the payload's `action` field.
         * @param value The payload's `value` field; required by [Navigate] and [EvaluateJs],
         *   ignored by the rest.
         * @return The decoded command, or `null` if [action] is unrecognised or a required
         *   [value] is missing or blank.
         */
        public fun of(action: String, value: String?): RemoteCommandModel? = when (action.trim().lowercase()) {
            ACTION_NAVIGATE -> value?.trim()?.takeIf { it.isNotEmpty() }?.let(::Navigate)
            ACTION_RELOAD -> Reload
            ACTION_BACK -> Back
            ACTION_FORWARD -> Forward
            ACTION_CLEAR_CACHE -> ClearCache
            ACTION_NAVIGATE_HOME -> NavigateHome
            ACTION_EVALUATE_JS -> value?.takeIf { it.isNotBlank() }?.let(::EvaluateJs)
            else -> null
        }
    }
}
