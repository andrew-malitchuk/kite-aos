package presentation.feature.main.source.webview

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

/**
 * State holder for the [KioskWebView].
 *
 * It manages the URL, whitelist, loading state, and navigation history.
 */
@Stable
public class KioskWebViewState(
    initialUrl: String = "http://192.168.68.125:8123/",
    initialWhitelist: List<String> = listOf("192.168.68.125", "127.0.0.1")
) {
    /** The current URL to load in the WebView. */
    public var url: String by mutableStateOf(initialUrl)
    /** The list of domains that are allowed to be navigated to. */
    public var whitelist: List<String> by mutableStateOf(initialWhitelist)
    /** Indicates if the WebView is currently loading a page. */
    public var isLoading: Boolean by mutableStateOf(true)
    /** Indicates if the WebView can navigate backward in history. */
    public var canGoBack: Boolean by mutableStateOf(false)
    /** Indicates if the WebView can navigate forward in history. */
    public var canGoForward: Boolean by mutableStateOf(false)
    internal var webView: WebView? = null

    /** Reload the current page. */
    public fun reload() {
        webView?.reload()
    }

    /** Navigate backward in the history. */
    public fun goBack() {
        webView?.goBack()
    }

    /** Navigate forward in the history. */
    public fun goForward() {
        webView?.goForward()
    }
}

/**
 * Creates and remembers a [KioskWebViewState].
 */
@Composable
public fun rememberKioskWebViewState(
    url: String = "http://192.168.68.125:8123/",
    whitelist: List<String> = listOf("192.168.68.125", "127.0.0.1")
): KioskWebViewState = remember {
    KioskWebViewState(url, whitelist)
}

/**
 * A customized WebView component for kiosk use cases.
 *
 * It features a restricted navigation system (whitelisting) and exposes its
 * state (loading status, navigation history) via [KioskWebViewState].
 *
 * @param state The [KioskWebViewState] to be used.
 * @param modifier The modifier for the WebView container.
 */
@Composable
public fun KioskWebView(
    state: KioskWebViewState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    state.webView = this

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: android.graphics.Bitmap?
                        ) {
                            state.isLoading = true
                            state.canGoBack = view?.canGoBack() ?: false
                            state.canGoForward = view?.canGoForward() ?: false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            state.isLoading = false
                            state.canGoBack = view?.canGoBack() ?: false
                            state.canGoForward = view?.canGoForward() ?: false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val newUrl = request?.url?.toString() ?: ""
                            val isAllowed =
                                state.whitelist.any { domain -> newUrl.contains(domain) }
                            return !isAllowed
                        }
                    }

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    loadUrl(state.url)
                }
            },
            update = { view ->
                if (view.url != state.url) {
                    view.loadUrl(state.url)
                }
            }
        )
    }
}