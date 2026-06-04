package presentation.feature.main.source.webview

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Engine-agnostic state holder for [KioskWebView].
 *
 * Replaces [KioskWebViewState] as the public API surface consumed by [presentation.feature.main.source.main.MainContent].
 * Both [AndroidWebViewEngine] and [GeckoViewEngine] write into this state via [EngineHandle].
 *
 * @param initialUrl The default URL to load when the engine is first created.
 * @param initialWhitelist The default list of allowed domains for navigation.
 * @since 0.0.4
 */
@Stable
public class KioskEngineState(
    initialUrl: String = "",
    initialWhitelist: List<String> = emptyList(),
) {
    /** The current URL to load in the engine. */
    public var url: String by mutableStateOf(initialUrl)

    /** The list of domains that are allowed to be navigated to. */
    public var whitelist: List<String> by mutableStateOf(initialWhitelist)

    /** Whether to bypass SSL certificate validation (for self-signed certs on local HA instances). */
    public var trustAllSsl: Boolean by mutableStateOf(false)

    /** Whether the engine is currently loading a page. */
    public var isLoading: Boolean by mutableStateOf(false)

    /**
     * The URL of the most recently finished page load.
     * Empty string before the first page load completes.
     */
    public var currentUrl: String by mutableStateOf("")

    /** Whether the engine can navigate backward in history. */
    public var canGoBack: Boolean by mutableStateOf(false)

    /** Whether the engine can navigate forward in history. */
    public var canGoForward: Boolean by mutableStateOf(false)

    /**
     * Whether the engine encountered a load error on the current page.
     * Reset to `false` on the next [onPageStarted] call.
     */
    public var isError: Boolean by mutableStateOf(false)

    /** The active engine handle — set by whichever engine is mounted. */
    internal var engine: EngineHandle? = null

    // --- AndroidWebViewEngine-specific state (ignored by GeckoViewEngine) ---

    /**
     * A child WebView created by [android.webkit.WebChromeClient.onCreateWindow].
     * Non-null while a popup/dialog spawned by the page is open (e.g. HA camera modal).
     */
    internal var childWebView: WebView? by mutableStateOf(null)

    /**
     * The View provided by [android.webkit.WebChromeClient.onShowCustomView].
     * Non-null while the page has an active fullscreen element (e.g. a camera stream).
     */
    internal var fullscreenView: View? by mutableStateOf(null)
    internal var fullscreenCallback: WebChromeClient.CustomViewCallback? = null

    /** Reload the current page. */
    public fun reload() {
        engine?.reload()
    }

    /** Navigate backward in history. */
    public fun goBack() {
        engine?.goBack()
    }

    /** Navigate forward in history. */
    public fun goForward() {
        engine?.goForward()
    }
}

/**
 * Minimal interface implemented by each concrete engine to expose navigation commands
 * back to [KioskEngineState].
 */
internal interface EngineHandle {
    fun reload()
    fun goBack()
    fun goForward()
}
