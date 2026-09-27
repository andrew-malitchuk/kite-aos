package presentation.core.platform.source.command

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

/**
 * In-process event bus for hardware-remote (D-pad) commands that originate outside
 * the feature composition tree.
 *
 * On Android TV the settings-bar gesture is a long-press key combo detected in
 * `HostActivity.dispatchKeyEvent` — above the navigation graph and the Main feature.
 * This bus carries that intent to `MainViewModel`, mirroring how the MQTT FAB command
 * reaches the same screen, without threading an Activity callback through navigation.
 *
 * A small buffer with no replay is used: emissions are transient user actions, only
 * meaningful while the kiosk screen is active and collecting.
 *
 * @since 1.2.0
 */
@Single
public class RemoteCommandBus {

    private val _openDrawer = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions requesting the control drawer (settings bar) be opened.
     *
     * @since 1.2.0
     */
    public val openDrawer: SharedFlow<Unit> = _openDrawer.asSharedFlow()

    private val _motion = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions representing an external motion pulse (e.g. an MQTT presence command).
     *
     * Kept here rather than injecting the MQTT use case directly into the TV motion source,
     * which would otherwise break koin-annotations KSP generation for the tv variant.
     *
     * @since 1.2.0
     */
    public val motion: SharedFlow<Unit> = _motion.asSharedFlow()

    private val _clearCache = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions requesting the WebView cache be flushed and the dashboard reloaded.
     *
     * Raised by the Home Assistant `clear_cache` button. The cache lives inside whichever engine
     * the Main feature has mounted, which `MqttService` cannot reach directly, so the request
     * travels the same route as the MQTT FAB command.
     *
     * @since 2.1.0
     */
    public val clearCache: SharedFlow<Unit> = _clearCache.asSharedFlow()

    /**
     * Requests that the control drawer open. Safe to call from any thread.
     *
     * @since 1.2.0
     */
    public fun emitOpenDrawer() {
        _openDrawer.tryEmit(Unit)
    }

    /**
     * Requests a WebView cache flush and reload. Safe to call from any thread.
     *
     * @since 2.1.0
     */
    public fun emitClearCache() {
        _clearCache.tryEmit(Unit)
    }

    private val _reload = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions requesting a plain dashboard reload.
     *
     * Raised by the scheduled daily reload and by memory-pressure recovery. A reload re-requests
     * the page the panel is currently on, so the dashboard's path survives — the user is not
     * bounced back to the configured home URL.
     *
     * @since 2.2.0
     */
    public val reload: SharedFlow<Unit> = _reload.asSharedFlow()

    /**
     * Requests a dashboard reload. Safe to call from any thread.
     *
     * @since 2.2.0
     */
    public fun emitReload() {
        _reload.tryEmit(Unit)
    }

    private val _homeReset = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions requesting the dashboard return to its configured home URL.
     *
     * Unlike [reload], this deliberately discards the current path — it is the inactivity reset,
     * whose whole purpose is to undo wherever a passer-by navigated to.
     *
     * @since 2.2.0
     */
    public val homeReset: SharedFlow<Unit> = _homeReset.asSharedFlow()

    /**
     * Requests a return to the home URL. Safe to call from any thread.
     *
     * @since 2.2.0
     */
    public fun emitHomeReset() {
        _homeReset.tryEmit(Unit)
    }

    private val _navigate = MutableSharedFlow<String>(extraBufferCapacity = 1)

    /**
     * Emissions carrying a URL the dashboard should load.
     *
     * Raised by the MQTT `navigate` command. Unlike [homeReset] the destination is arbitrary, so
     * the URL travels with the event rather than being read back from preferences.
     *
     * @since 2.2.0
     */
    public val navigate: SharedFlow<String> = _navigate.asSharedFlow()

    /**
     * Requests the dashboard load [url]. Safe to call from any thread.
     *
     * @param url The address to load.
     * @since 2.2.0
     */
    public fun emitNavigate(url: String) {
        _navigate.tryEmit(url)
    }

    private val _back = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions requesting a step back through the WebView history.
     *
     * @since 2.2.0
     */
    public val back: SharedFlow<Unit> = _back.asSharedFlow()

    /**
     * Requests a step back in history. Safe to call from any thread.
     *
     * @since 2.2.0
     */
    public fun emitBack() {
        _back.tryEmit(Unit)
    }

    private val _forward = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions requesting a step forward through the WebView history.
     *
     * @since 2.2.0
     */
    public val forward: SharedFlow<Unit> = _forward.asSharedFlow()

    /**
     * Requests a step forward in history. Safe to call from any thread.
     *
     * @since 2.2.0
     */
    public fun emitForward() {
        _forward.tryEmit(Unit)
    }

    private val _evaluateJs = MutableSharedFlow<String>(extraBufferCapacity = 1)

    /**
     * Emissions carrying a JavaScript snippet to run inside the loaded page.
     *
     * Raised by the MQTT `evaluate_js` command. Honoured by the Android WebView engine only —
     * GeckoView exposes no equivalent entry point and ignores the snippet.
     *
     * @since 2.2.0
     */
    public val evaluateJs: SharedFlow<String> = _evaluateJs.asSharedFlow()

    /**
     * Requests that [script] run in the loaded page. Safe to call from any thread.
     *
     * @param script The JavaScript snippet to evaluate.
     * @since 2.2.0
     */
    public fun emitEvaluateJs(script: String) {
        _evaluateJs.tryEmit(script)
    }

    private val _interaction = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions signalling deliberate user input that should reset the inactivity countdown.
     *
     * Needed for Android TV, where input arrives as D-pad key events at `HostActivity` — above the
     * navigation graph — and never reaches the Compose pointer pipeline the touch path uses.
     *
     * @since 2.2.0
     */
    public val interaction: SharedFlow<Unit> = _interaction.asSharedFlow()

    /**
     * Signals deliberate user input. Safe to call from any thread.
     *
     * @since 2.2.0
     */
    public fun emitInteraction() {
        _interaction.tryEmit(Unit)
    }

    /**
     * Signals an external motion pulse. Safe to call from any thread.
     *
     * @since 1.2.0
     */
    public fun emitMotion() {
        _motion.tryEmit(Unit)
    }
}
