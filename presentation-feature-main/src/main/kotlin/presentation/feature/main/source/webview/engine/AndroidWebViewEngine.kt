package presentation.feature.main.source.webview.engine

import android.net.http.SslError
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import presentation.feature.main.BuildConfig
import presentation.feature.main.source.webview.EngineHandle
import presentation.feature.main.source.webview.KioskEngineState

/**
 * Android system WebView implementation of the kiosk browser engine.
 *
 * Renders the Home Assistant dashboard using the system WebView with custom settings,
 * JS injection for HA shadow DOM fixes, popup handling, fullscreen video, SSL bypass,
 * and cookie management.
 *
 * @param state The [KioskEngineState] managing URL, whitelist, and navigation state.
 * @param modifier Modifier applied to the root [Box].
 * @see KioskEngineState
 * @since 0.0.4
 */
@Composable
internal fun AndroidWebViewEngine(state: KioskEngineState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true)
                WebView(context).apply {
                    state.engine = object : EngineHandle {
                        override fun reload() = this@apply.reload()
                        override fun goBack() = this@apply.goBack()
                        override fun goForward() = this@apply.goForward()
                    }

                    // addDocumentStartJavaScript runs BEFORE any page scripts — unlike
                    // evaluateJavascript() which is queued and may run after HA's ES modules
                    // have already registered their custom elements.
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
                        mediaPlaybackRequiresUserGesture = false
                        setSupportMultipleWindows(true)
                        javaScriptCanOpenWindowsAutomatically = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString =
                            "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/137.0.0.0 Mobile Safari/537.36"
                        setSafeBrowsingEnabled(false)
                        cacheMode = WebSettings.LOAD_NO_CACHE
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onPermissionRequest(request: PermissionRequest?) {
                            if (BuildConfig.DEBUG) {
                                Log.d("AndroidWebViewEngine", "onPermissionRequest: ${request?.resources?.toList()}")
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
                                    "AndroidWebViewEngine/JS",
                                    "${msg.message()} — ${msg.sourceId()}:${msg.lineNumber()}",
                                )
                            }
                            return true
                        }

                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: Message?,
                        ): Boolean {
                            if (BuildConfig.DEBUG) {
                                Log.d("AndroidWebViewEngine", "onCreateWindow isDialog=$isDialog isUserGesture=$isUserGesture")
                            }
                            val child = WebView(view!!.context).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    mediaPlaybackRequiresUserGesture = false
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
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
                                        if (state.trustAllSsl) handler?.proceed() else handler?.cancel()
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

                        override fun onShowCustomView(
                            view: View?,
                            callback: CustomViewCallback?,
                        ) {
                            if (BuildConfig.DEBUG) {
                                Log.d("AndroidWebViewEngine", "onShowCustomView view=${view?.javaClass?.simpleName}")
                            }
                            state.fullscreenCallback?.onCustomViewHidden()
                            state.fullscreenView = view
                            state.fullscreenCallback = callback
                        }

                        override fun onHideCustomView() {
                            if (BuildConfig.DEBUG) Log.d("AndroidWebViewEngine", "onHideCustomView")
                            state.fullscreenCallback?.onCustomViewHidden()
                            state.fullscreenView = null
                            state.fullscreenCallback = null
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: android.graphics.Bitmap?,
                        ) {
                            state.isLoading = true
                            state.isError = false
                            state.canGoBack = view?.canGoBack() ?: false
                            state.canGoForward = view?.canGoForward() ?: false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?,
                        ) {
                            // Only treat main-frame errors as fatal page errors.
                            if (request?.isForMainFrame == true) {
                                state.isError = true
                                if (BuildConfig.DEBUG) {
                                    Log.w(
                                        "AndroidWebViewEngine",
                                        "onReceivedError: ${error?.errorCode} ${error?.description}",
                                    )
                                }
                            }
                        }

                        override fun onReceivedHttpError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            errorResponse: WebResourceResponse?,
                        ) {
                            // Treat HTTP 5xx on the main frame as an error.
                            val statusCode = errorResponse?.statusCode ?: return
                            if (request?.isForMainFrame == true && statusCode >= 500) {
                                state.isError = true
                                if (BuildConfig.DEBUG) {
                                    Log.w("AndroidWebViewEngine", "onReceivedHttpError: $statusCode")
                                }
                            }
                        }

                        override fun onRenderProcessGone(
                            view: WebView?,
                            detail: RenderProcessGoneDetail?,
                        ): Boolean {
                            state.isError = true
                            if (BuildConfig.DEBUG) {
                                Log.e("AndroidWebViewEngine", "onRenderProcessGone didCrash=${detail?.didCrash()}")
                            }
                            // Return true to indicate we handled the crash gracefully.
                            return true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            state.isLoading = false
                            state.canGoBack = view?.canGoBack() ?: false
                            state.canGoForward = view?.canGoForward() ?: false
                            if (!url.isNullOrEmpty()) {
                                state.currentUrl = url
                            }
                            view?.evaluateJavascript(
                                """
                                (function() {
                                    function log(msg) { console.log('[KioskFix] ' + msg); }

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

                                    var fixedByUs = new WeakSet();
                                    var fixedElements = [];

                                    function isDialogTag(tag) {
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

                                    setInterval(function() {
                                        fixedElements.slice().forEach(function(el) {
                                            if (!isOpen(el)) restoreDialog(el);
                                        });
                                    }, 500);

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
                            if (state.trustAllSsl) handler?.proceed() else handler?.cancel()
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

        val childWebView = state.childWebView
        if (childWebView != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { childWebView },
                onRelease = { webView ->
                    webView.stopLoading()
                    webView.destroy()
                },
            )
        }

        val fullscreenView = state.fullscreenView
        if (fullscreenView != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { fullscreenView },
            )
        }
    }
}
