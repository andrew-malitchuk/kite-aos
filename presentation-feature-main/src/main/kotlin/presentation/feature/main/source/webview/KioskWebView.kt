package presentation.feature.main.source.webview

import android.net.http.SslError
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import presentation.feature.main.BuildConfig

/**
 * State holder for the [KioskWebView].
 *
 * It manages the URL, whitelist, loading state, and navigation history.
 * Marked as [Stable] to allow Compose to skip recomposition when the reference is unchanged.
 *
 * @param initialUrl The default URL to load when the WebView is first created.
 * @param initialWhitelist The default list of allowed domains for navigation.
 * @see KioskWebView
 * @see rememberKioskWebViewState
 * @since 0.0.1
 */
@Stable
public class KioskWebViewState(
    initialUrl: String = "",
    initialWhitelist: List<String> = emptyList(),
) {
    /** The current URL to load in the WebView. */
    public var url: String by mutableStateOf(initialUrl)

    /** The list of domains that are allowed to be navigated to. */
    public var whitelist: List<String> by mutableStateOf(initialWhitelist)

    /** Indicates if the WebView is currently loading a page. */
    public var isLoading: Boolean by mutableStateOf(false)

    /**
     * The URL of the most recently finished page load, updated by [onPageFinished].
     * Empty string before the first page load completes.
     */
    public var currentUrl: String by mutableStateOf("")

    /** Indicates if the WebView can navigate backward in history. */
    public var canGoBack: Boolean by mutableStateOf(false)

    /** Indicates if the WebView can navigate forward in history. */
    public var canGoForward: Boolean by mutableStateOf(false)
    internal var webView: WebView? = null

    /**
     * A child WebView created by [android.webkit.WebChromeClient.onCreateWindow].
     * Non-null while a popup/dialog spawned by the page is open (e.g. HA camera modal).
     * Set to null by [android.webkit.WebChromeClient.onCloseWindow].
     */
    internal var childWebView: WebView? by mutableStateOf(null)

    /**
     * The View provided by [android.webkit.WebChromeClient.onShowCustomView].
     * Non-null while the page has an active fullscreen element (e.g. a camera stream).
     * Dismissed by [android.webkit.WebChromeClient.onHideCustomView] or user action.
     */
    internal var fullscreenView: View? by mutableStateOf(null)
    internal var fullscreenCallback: WebChromeClient.CustomViewCallback? = null

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
 * Creates and remembers a [KioskWebViewState] instance across recompositions.
 *
 * @param url The initial URL to load in the WebView.
 * @param whitelist The list of allowed domains for URL navigation.
 * @return A remembered [KioskWebViewState] instance.
 * @see KioskWebViewState
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun rememberKioskWebViewState(
    url: String = "",
    whitelist: List<String> = emptyList(),
): KioskWebViewState = remember {
    KioskWebViewState(url, whitelist)
}

/**
 * A customized WebView component for kiosk use cases.
 *
 * It features a restricted navigation system (whitelisting) and exposes its
 * state (loading status, navigation history) via [KioskWebViewState].
 * JavaScript and DOM storage are enabled. A desktop Chrome UA is used so that
 * Home Assistant renders streams natively via WebRTC instead of expecting a
 * native ExoPlayer bridge. Third-party cookies are accepted to support HA auth.
 *
 * @param state The [KioskWebViewState] managing URL, whitelist, and navigation.
 * @param modifier Modifier to be applied to the [Box] root composable.
 * @see KioskWebViewState
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun KioskWebView(state: KioskWebViewState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                // Enable remote debugging via chrome://inspect on debug builds.
                // Connect while the app is running to inspect DOM/CSS inside the WebView.
                if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true)
                WebView(context).apply {
                    state.webView = this

                    // addDocumentStartJavaScript runs BEFORE any page scripts — unlike
                    // evaluateJavascript() which is queued and may run after HA's ES modules
                    // have already registered their custom elements.
                    // We intercept customElements.define() so that when ha-camera-stream and
                    // ha-hls-player register themselves, their allowExoplayer property is
                    // permanently overridden to return false. Without this, HA detects the
                    // Android UA and sets allowExoplayer=true, which hides the <video> element
                    // (height:0) while waiting for a native ExoPlayer bridge that doesn't exist.
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
                        WebViewCompat.addDocumentStartJavaScript(
                            this,
                            """
                            (function() {
                                // 1. Patch allowExoplayer so HA uses its web video stack
                                //    instead of waiting for a native ExoPlayer bridge.
                                var _define = customElements.define.bind(customElements);
                                customElements.define = function(name, ctor, opts) {
                                    if (name === 'ha-camera-stream' || name === 'ha-hls-player') {
                                        Object.defineProperty(ctor.prototype, 'allowExoplayer', {
                                            get: function() { return false; },
                                            set: function() {},
                                            configurable: true
                                        });
                                    }
                                    return _define(name, ctor, opts);
                                };

                                // 2. Inject CSS as soon as <head> exists.
                                //    In WebView the body computes to height:0 because
                                //    nothing in the ancestor chain establishes a 100vh
                                //    block-level context. ha-dialog gets display:inline
                                //    (custom-element default) instead of block, making the
                                //    dialog invisible even though it is technically open.
                                var cssText = [
                                    'html { height: 100vh !important; min-height: 100vh !important; }',
                                    'body { height: 100% !important; min-height: 100vh !important; overflow: auto !important; }'
                                ].join(' ');
                                function injectCss() {
                                    if (document.head && !document.getElementById('__kite_fix')) {
                                        var s = document.createElement('style');
                                        s.id = '__kite_fix';
                                        s.textContent = cssText;
                                        document.head.appendChild(s);
                                        return true;
                                    }
                                    return false;
                                }
                                if (!injectCss()) {
                                    var obs = new MutationObserver(function(_, o) {
                                        if (injectCss()) o.disconnect();
                                    });
                                    obs.observe(document.documentElement, { childList: true, subtree: true });
                                }
                            })();
                            """.trimIndent(),
                            setOf("*"),
                        )
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        // Allow video streams to play without requiring a user gesture
                        mediaPlaybackRequiresUserGesture = false
                        // Required so onCreateWindow fires for HA's camera popup dialogs.
                        // Without this, window.open() / showModalDialog calls are silently dropped.
                        setSupportMultipleWindows(true)
                        // Must be set together with setSupportMultipleWindows(true); without this
                        // some WebView versions silently ignore window.open() calls from JS.
                        javaScriptCanOpenWindowsAutomatically = true
                        // Allow mixed HTTP/HTTPS content (e.g. camera feeds served over HTTP inside HA)
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        // Use a standard Android Chrome UA without the "wv" WebView marker.
                        // Chrome works with this UA and HA renders identically to Chrome mobile.
                        // The "wv" marker in the default WebView UA can cause HA or other dashboards
                        // to apply WebView-specific workarounds that break video rendering.
                        // ExoPlayer mode is NOT triggered by UA alone — it requires the HA companion
                        // app External Bus (window.externalApp) which we do not register.
                        userAgentString =
                            "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/137.0.0.0 Mobile Safari/537.36"
                        // Prevents Safe Browsing from intercepting local network requests
                        // (e.g. LAN IP addresses that may trigger phishing heuristics).
                        setSafeBrowsingEnabled(false)
                        // Disable cache to prevent stale MediaSource state causing black video.
                        cacheMode = WebSettings.LOAD_NO_CACHE
                    }

                    // Do NOT set layerType explicitly. Setting LAYER_TYPE_HARDWARE can conflict
                    // with the WebView's internal hardware-decoded video surface management —
                    // the decoder outputs to its own SurfaceTexture and the explicit hardware
                    // layer wrapping can cause the video frames to be composited incorrectly
                    // (renders black). android:hardwareAccelerated="true" on <application> is
                    // sufficient; the WebView manages its own video compositing internally.

                    // Required for WebRTC-based camera streams in Home Assistant.
                    // onPermissionRequest grants PROTECTED_MEDIA_ID and VIDEO_CAPTURE
                    // so HA can negotiate and render WebRTC streams inside the WebView.
                    webChromeClient = object : WebChromeClient() {
                        override fun onPermissionRequest(request: PermissionRequest?) {
                            if (BuildConfig.DEBUG) {
                                Log.d("KioskWebView", "onPermissionRequest: ${request?.resources?.toList()}")
                            }
                            request?.grant(request.resources)
                        }

                        override fun onConsoleMessage(msg: ConsoleMessage?): Boolean {
                            if (BuildConfig.DEBUG && msg != null) {
                                val level = when (msg.messageLevel()) {
                                    ConsoleMessage.MessageLevel.ERROR -> Log.ERROR
                                    ConsoleMessage.MessageLevel.WARNING -> Log.WARN
                                    else -> Log.DEBUG
                                }
                                Log.println(
                                    level,
                                    "KioskWebView/JS",
                                    "${msg.message()} — ${msg.sourceId()}:${msg.lineNumber()}",
                                )
                            }
                            return true
                        }

                        // HA camera cards open in a popup window (window.open / showModalDialog).
                        // Without onCreateWindow, the WebView silently drops the call and the
                        // camera dialog never appears — the user sees only the card title.
                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: Message?,
                        ): Boolean {
                            if (BuildConfig.DEBUG) {
                                Log.d("KioskWebView", "onCreateWindow isDialog=$isDialog isUserGesture=$isUserGesture")
                            }
                            val child = WebView(view!!.context).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    mediaPlaybackRequiresUserGesture = false
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    // Must match the parent UA — child WebView otherwise gets the
                                    // default "wv"-marked WebView UA which can break video rendering.
                                    userAgentString =
                                        "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                                        "(KHTML, like Gecko) Chrome/137.0.0.0 Mobile Safari/537.36"
                                }
                                webChromeClient = object : WebChromeClient() {
                                    override fun onPermissionRequest(request: PermissionRequest?) {
                                        request?.grant(request.resources)
                                    }

                                    override fun onCloseWindow(window: WebView?) {
                                        state.childWebView = null
                                    }
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onReceivedSslError(
                                        view: WebView?,
                                        handler: SslErrorHandler?,
                                        error: SslError?,
                                    ) {
                                        handler?.proceed()
                                    }
                                }
                            }
                            val transport = resultMsg?.obj as? WebView.WebViewTransport
                            transport?.webView = child
                            resultMsg?.sendToTarget()
                            state.childWebView = child
                            return true
                        }

                        override fun onCloseWindow(window: WebView?) {
                            state.childWebView = null
                        }

                        // Called when the page requests fullscreen via element.requestFullscreen()
                        // or via a <video> element entering fullscreen. Without this, WebView
                        // silently ignores the request — the user sees nothing happen.
                        override fun onShowCustomView(
                            view: View?,
                            callback: CustomViewCallback?,
                        ) {
                            if (BuildConfig.DEBUG) {
                                Log.d("KioskWebView", "onShowCustomView view=${view?.javaClass?.simpleName}")
                            }
                            state.fullscreenCallback?.onCustomViewHidden()
                            state.fullscreenView = view
                            state.fullscreenCallback = callback
                        }

                        override fun onHideCustomView() {
                            if (BuildConfig.DEBUG) Log.d("KioskWebView", "onHideCustomView")
                            state.fullscreenCallback?.onCustomViewHidden()
                            state.fullscreenView = null
                            state.fullscreenCallback = null
                        }
                    }

                    webViewClient =
                        object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: android.graphics.Bitmap?,
                            ) {
                                state.isLoading = true
                                state.canGoBack = view?.canGoBack() ?: false
                                state.canGoForward = view?.canGoForward() ?: false
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                state.isLoading = false
                                state.canGoBack = view?.canGoBack() ?: false
                                state.canGoForward = view?.canGoForward() ?: false
                                if (!url.isNullOrEmpty()) {
                                    state.currentUrl = url
                                }
                                // The <video> plays (readyState:4, paused:false) but has height:0.
                                // HA sets height:0 on the video (or its shadow host) when
                                // allowExoplayer=true — either via inline style OR CSS class.
                                // We must use setProperty('important') to override both cases,
                                // and also fix the shadow host elements in the parent chain,
                                // since height:100% on a video inside a height:0 host stays 0.
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        function log(msg) { console.log('[KioskFix] ' + msg); }

                                        // Re-apply body height fix.
                                        document.documentElement.style.setProperty('height', '100vh', 'important');
                                        document.documentElement.style.setProperty('min-height', '100vh', 'important');
                                        if (document.body) {
                                            document.body.style.setProperty('height', '100%', 'important');
                                            document.body.style.setProperty('min-height', '100vh', 'important');
                                            document.body.style.setProperty('overflow', 'auto', 'important');
                                        }

                                        var STYLE_ID = '__kite_fix';
                                        var DIALOG_PROPS = ['display','position','top','left','width','height','z-index','overflow'];

                                        function injectStyle(sr, css) {
                                            if (!sr || sr.querySelector('#' + STYLE_ID)) return;
                                            var s = document.createElement('style');
                                            s.id = STYLE_ID;
                                            s.textContent = css;
                                            sr.appendChild(s);
                                        }

                                        var CAM_CSS =
                                            ':host{display:block!important;height:240px!important;min-height:240px!important;overflow:hidden!important}' +
                                            'video{display:block!important;width:100%!important;height:240px!important;min-height:240px!important;object-fit:cover!important}';

                                        var DIALOG_CSS =
                                            ':host{display:block!important;width:100%!important;height:100%!important}' +
                                            'ha-dialog{display:block!important;width:100%!important;height:100%!important}' +
                                            'dialog,[role="dialog"]{display:flex!important;flex-direction:column!important;width:100%!important;height:100%!important;max-width:100%!important;max-height:100%!important;margin:0!important;border:none!important;padding:0!important;position:relative!important;inset:auto!important}' +
                                            '.mdc-dialog__container,.container,.surface,.mdc-dialog__surface{height:100%!important;max-height:100%!important;width:100%!important;display:flex!important;flex-direction:column!important}' +
                                            '.mdc-dialog__content,.content,.body,[class*="scroll"]{flex:1 1 auto!important;max-height:none!important;overflow-y:auto!important}';

                                        var CAMERA_TAGS = new Set(['ha-more-info-camera','ha-camera-stream','ha-webrtc-player','ha-hls-player']);

                                        // Track which elements we have forcefully positioned so we can
                                        // restore them when HA closes the dialog. Without this, the
                                        // invisible fixed overlay blocks all touches on the page.
                                        // WeakSet for O(1) has()-checks; plain Array for iteration
                                        // (needed by the cleanup interval — WeakSet is not iterable).
                                        var fixedByUs = new WeakSet();
                                        var fixedElements = [];

                                        function isDialogTag(tag) {
                                            // Match ha-dialog, ha-more-info-dialog, ha-dialog-box, etc.
                                            return tag === 'ha-dialog' || (tag.startsWith('ha-') && tag.includes('dialog'));
                                        }

                                        function isOpen(el) {
                                            var cs = window.getComputedStyle(el);
                                            return cs.display !== 'none' &&
                                                   cs.visibility !== 'hidden' &&
                                                   el.getAttribute('aria-hidden') !== 'true';
                                        }

                                        function forceFullscreen(el) {
                                            fixedByUs.add(el);
                                            fixedElements.push(el);
                                            // Do NOT set display:block !important here.
                                            // position:fixed already blockifies display:inline per CSS spec,
                                            // and forcing display:block !important prevents isOpen() from
                                            // detecting when HA closes the dialog via style="display:none" —
                                            // our !important would win, getComputedStyle returns 'block',
                                            // isOpen() returns true, restoreDialog is never called, and
                                            // the invisible fixed overlay blocks all subsequent touches.
                                            el.style.setProperty('position', 'fixed',  'important');
                                            el.style.setProperty('top',      '0',      'important');
                                            el.style.setProperty('left',     '0',      'important');
                                            el.style.setProperty('width',    '100vw',  'important');
                                            el.style.setProperty('height',   '100vh',  'important');
                                            el.style.setProperty('z-index',  '9999',   'important');
                                            el.style.setProperty('overflow', 'hidden', 'important');
                                        }

                                        function restoreDialog(el) {
                                            if (!fixedByUs.has(el)) return;
                                            fixedByUs.delete(el);
                                            var idx = fixedElements.indexOf(el);
                                            if (idx !== -1) fixedElements.splice(idx, 1);
                                            DIALOG_PROPS.forEach(function(p){ el.style.removeProperty(p); });
                                            log('dialog restored: ' + el.tagName.toLowerCase());
                                        }

                                        // Watch a dialog element for HA closing it (display:none, aria-hidden,
                                        // class change, or removal from DOM). When closed, remove our forced
                                        // styles so the invisible overlay doesn't block touches.
                                        function watchForClose(el) {
                                            var attrObs = new MutationObserver(function() {
                                                if (!isOpen(el)) {
                                                    restoreDialog(el);
                                                    attrObs.disconnect();
                                                }
                                            });
                                            attrObs.observe(el, {
                                                attributes: true,
                                                attributeFilter: ['style','class','aria-hidden','hidden']
                                            });
                                            // Also watch parent in case HA removes the element from DOM.
                                            if (el.parentNode) {
                                                var domObs = new MutationObserver(function() {
                                                    if (!el.isConnected) {
                                                        restoreDialog(el);
                                                        attrObs.disconnect();
                                                        domObs.disconnect();
                                                    }
                                                });
                                                domObs.observe(el.parentNode, { childList: true });
                                            }
                                        }

                                        // insideDialog: true when recursing inside a dialog shadow root.
                                        // CAM_CSS must NOT be injected inside popups — the popup controls
                                        // the camera element size, and forcing height:240px + overflow:hidden
                                        // clips the video/error-state UI. After 2-3 openings the setInterval
                                        // traversal injects the style permanently into the shadow root, which
                                        // makes it look like the stream stopped working (nothing visible).
                                        // CAM_CSS is only needed on the main dashboard where HA sets height:0.
                                        function traverse(root, insideDialog) {
                                            root.querySelectorAll('*').forEach(function(el) {
                                                var sr = el.shadowRoot;
                                                if (!sr) return;
                                                var tag = el.tagName.toLowerCase();

                                                if (CAMERA_TAGS.has(tag)) {
                                                    if (!insideDialog) injectStyle(sr, CAM_CSS);
                                                } else if (isDialogTag(tag)) {
                                                    injectStyle(sr, DIALOG_CSS);
                                                    if (isOpen(el) && !fixedByUs.has(el)) {
                                                        var r = el.getBoundingClientRect();
                                                        log('dlg open ' + tag + ' ' + r.width + 'x' + r.height + '@' + Math.round(r.top));
                                                        forceFullscreen(el);
                                                        watchForClose(el);
                                                    }
                                                }
                                                traverse(sr, insideDialog || isDialogTag(tag));
                                            });
                                        }

                                        traverse(document, false);
                                        setTimeout(function(){ traverse(document, false); }, 800);
                                        setTimeout(function(){ traverse(document, false); }, 2500);

                                        // Fast cleanup interval: iterates every fixed element and restores
                                        // any that are now closed. This is the safety net for the common
                                        // case where watchForClose() MutationObserver never fires because
                                        // HA closes the dialog via shadow-DOM state changes or animations
                                        // rather than attribute changes on the host element itself.
                                        // Without this, position:fixed;z-index:9999 lingers after dialog
                                        // close and blocks all subsequent touches until reload.
                                        // WeakSet is not iterable, so we use the parallel fixedElements array.
                                        setInterval(function() {
                                            fixedElements.slice().forEach(function(el) {
                                                if (!isOpen(el)) restoreDialog(el);
                                            });
                                        }, 500);

                                        // Slow discovery interval: finds dialogs opened after page load.
                                        setInterval(function(){ traverse(document, false); }, 3000);
                                    })();
                                    """.trimIndent(),
                                    null,
                                )
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?,
                            ): Boolean {
                                val newUrl = request?.url?.toString() ?: ""
                                // Always allow blob: and data: URLs — these are used by hls.js
                                // for its transmuxer Web Worker and by go2rtc for HLS playlists.
                                // Blocking them silently breaks video stream initialization.
                                if (newUrl.startsWith("blob:") || newUrl.startsWith("data:")) {
                                    return false
                                }
                                if (state.whitelist.isEmpty()) return false
                                val isAllowed =
                                    state.whitelist.any { domain -> newUrl.contains(domain) }
                                return !isAllowed
                            }

                            override fun onReceivedSslError(
                                view: WebView?,
                                handler: SslErrorHandler?,
                                error: SslError?,
                            ) {
                                // Proceed past SSL errors for local HA instances using self-signed certs
                                handler?.proceed()
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
            },
        )

        // Overlay the child WebView (e.g. HA camera popup) on top of the main WebView.
        // It is only composed when onCreateWindow fires and dismissed via onCloseWindow.
        val childWebView = state.childWebView
        if (childWebView != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { childWebView },
                onRelease = { webView ->
                    // Explicitly destroy the child WebView when it leaves composition.
                    // Without this, the WebView object is never destroyed — its WebRTC
                    // PeerConnection stays alive on the go2rtc side. Each open/close cycle
                    // of the Bubble Card (or any camera popup) accumulates a live connection.
                    // After 2-3 cycles go2rtc hits its concurrent-connection limit and rejects
                    // new stream requests, so the popup opens but shows nothing.
                    webView.stopLoading()
                    webView.destroy()
                },
            )
        }

        // Fullscreen overlay: covers the entire Box when the page calls
        // element.requestFullscreen() (e.g. HA camera stream going fullscreen).
        // onShowCustomView provides a pre-rendered View from the WebView engine;
        // we just need to display it at full size.
        val fullscreenView = state.fullscreenView
        if (fullscreenView != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { fullscreenView },
            )
        }
    }
}
