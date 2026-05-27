package presentation.feature.main.source.webview.engine

import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSession.ContentDelegate
import org.mozilla.geckoview.GeckoSession.NavigationDelegate
import org.mozilla.geckoview.GeckoSession.ProgressDelegate
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.GeckoView
import presentation.feature.main.BuildConfig
import presentation.feature.main.source.webview.EngineHandle
import presentation.feature.main.source.webview.KioskEngineState

private const val TAG = "GeckoViewEngine"
private const val GECKO_CRASH_RECOVERY_DELAY_MS = 1_000L

/**
 * Pre-warms the [GeckoRuntime] singleton during [android.app.Application.onCreate].
 *
 * Call this as early as possible in the application lifecycle so that the GPU compositor
 * process has time to start and stabilise before any [GeckoViewEngine] composable is shown.
 * Without pre-warming, [GeckoRuntime.create] runs synchronously on the main thread during the
 * first Compose composition, blocking the frame long enough to trigger an ANR when the device
 * is under memory pressure.
 *
 * Safe to call multiple times — subsequent calls are no-ops (singleton already exists).
 */
public fun preWarmGeckoRuntime(context: android.content.Context) {
    GeckoRuntimeHolder.getOrCreate(context.applicationContext)
}

/**
 * Mozilla GeckoView implementation of the kiosk browser engine.
 *
 * Provides better WebRTC camera stream support than the system WebView on older devices.
 * GeckoView handles Home Assistant auth and media natively — no JavaScript injection is used.
 *
 * **Important:** [GeckoRuntime] must be a process-wide singleton. It is created lazily here
 * and held in [GeckoRuntimeHolder]. Do not create additional runtimes — GeckoView enforces
 * one runtime per process and will throw if a second is created.
 *
 * @param state The [KioskEngineState] managing URL, whitelist, and navigation state.
 * @param modifier Modifier applied to the root [Box].
 * @see KioskEngineState
 * @since 0.0.4
 */
@Composable
internal fun GeckoViewEngine(state: KioskEngineState, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val runtime = remember {
        GeckoRuntimeHolder.getOrCreate(context.applicationContext)
    }

    val session = remember {
        GeckoSession(
            GeckoSessionSettings.Builder()
                .allowJavascript(true)
                .build(),
        ).also { it.settings.setSuspendMediaWhenInactive(false) }
    }

    DisposableEffect(session) {
        session.navigationDelegate = object : NavigationDelegate {
            override fun onLocationChange(
                session: GeckoSession,
                url: String?,
                perms: MutableList<GeckoSession.PermissionDelegate.ContentPermission>,
                hasUserGesture: Boolean,
            ) {
                if (!url.isNullOrEmpty()) {
                    state.currentUrl = url
                }
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                state.canGoBack = canGoBack
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                state.canGoForward = canGoForward
            }

            override fun onLoadRequest(
                session: GeckoSession,
                request: NavigationDelegate.LoadRequest,
            ): GeckoResult<AllowOrDeny>? {
                val url = request.uri
                if (url.startsWith("blob:") || url.startsWith("data:") || url.startsWith("javascript:")) {
                    return GeckoResult.fromValue(AllowOrDeny.ALLOW)
                }
                if (state.whitelist.isEmpty()) return GeckoResult.fromValue(AllowOrDeny.ALLOW)
                val isAllowed = state.whitelist.any { domain -> url.contains(domain) }
                return GeckoResult.fromValue(if (isAllowed) AllowOrDeny.ALLOW else AllowOrDeny.DENY)
            }
        }

        session.progressDelegate = object : ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                state.isLoading = true
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                state.isLoading = false
            }
        }

        session.contentDelegate = object : ContentDelegate {
            override fun onCrash(session: GeckoSession) {
                if (BuildConfig.DEBUG) Log.e(TAG, "GeckoSession crashed — recovering after delay")
                // Delay recovery to let the GPU process fully tear down before restarting.
                // Immediate session.open() after a crash can chain back into a broken GPU state
                // and block the main thread via SyncResumeResizeCompositor.
                Handler(Looper.getMainLooper()).postDelayed({
                    session.open(runtime)
                    if (state.url.isNotEmpty()) session.loadUri(state.url)
                }, GECKO_CRASH_RECOVERY_DELAY_MS)
            }

            override fun onKill(session: GeckoSession) {
                if (BuildConfig.DEBUG) Log.e(TAG, "GeckoSession killed — recovering after delay")
                Handler(Looper.getMainLooper()).postDelayed({
                    session.open(runtime)
                    if (state.url.isNotEmpty()) session.loadUri(state.url)
                }, GECKO_CRASH_RECOVERY_DELAY_MS)
            }
        }

        session.permissionDelegate = object : GeckoSession.PermissionDelegate {
            override fun onContentPermissionRequest(
                session: GeckoSession,
                perm: GeckoSession.PermissionDelegate.ContentPermission,
            ): GeckoResult<Int> {
                val decision = when (perm.permission) {
                    GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_INAUDIBLE,
                    GeckoSession.PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE,
                    GeckoSession.PermissionDelegate.PERMISSION_PERSISTENT_STORAGE,
                    GeckoSession.PermissionDelegate.PERMISSION_STORAGE_ACCESS,
                    GeckoSession.PermissionDelegate.PERMISSION_LOCAL_DEVICE_ACCESS,
                    GeckoSession.PermissionDelegate.PERMISSION_LOCAL_NETWORK_ACCESS,
                    GeckoSession.PermissionDelegate.PERMISSION_MEDIA_KEY_SYSTEM_ACCESS ->
                        GeckoSession.PermissionDelegate.ContentPermission.VALUE_ALLOW

                    else -> GeckoSession.PermissionDelegate.ContentPermission.VALUE_DENY
                }
                return GeckoResult.fromValue(decision)
            }

            override fun onMediaPermissionRequest(
                session: GeckoSession,
                uri: String,
                video: Array<GeckoSession.PermissionDelegate.MediaSource>?,
                audio: Array<GeckoSession.PermissionDelegate.MediaSource>?,
                callback: GeckoSession.PermissionDelegate.MediaCallback,
            ) {
                val hasCameraPermission = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED
                val hasAudioPermission = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.RECORD_AUDIO,
                ) == PackageManager.PERMISSION_GRANTED

                val videoGranted = video.isNullOrEmpty() || hasCameraPermission
                val audioGranted = audio.isNullOrEmpty() || hasAudioPermission

                if (videoGranted && audioGranted) {
                    callback.grant(video?.firstOrNull(), audio?.firstOrNull())
                } else {
                    callback.reject()
                }
            }
        }

        onDispose {
            session.navigationDelegate = null
            session.progressDelegate = null
            session.contentDelegate = null
            session.permissionDelegate = null
            session.close()
        }
    }

    // Fire exactly once per URL change. The factory opens the session synchronously,
    // so by the time this effect runs the session is ready to accept loadUri().
    // Keeping URL loading here (rather than in the AndroidView update callback) prevents
    // multiple rapid loadUri() calls caused by recompositions before onPageStart fires.
    LaunchedEffect(state.url) {
        if (state.url.isNotEmpty()) session.loadUri(state.url)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                GeckoView(ctx).also { geckoView ->
                    state.engine = object : EngineHandle {
                        override fun reload() = session.reload()
                        override fun goBack() = session.goBack()
                        override fun goForward() = session.goForward()
                    }
                    session.open(runtime)
                    geckoView.setSession(session)
                }
            },
        )
    }
}

/**
 * Process-scoped singleton holder for [GeckoRuntime].
 * GeckoView enforces one runtime per Android process.
 */
private object GeckoRuntimeHolder {
    @Volatile
    private var instance: GeckoRuntime? = null

    fun getOrCreate(appContext: android.content.Context): GeckoRuntime {
        return instance ?: synchronized(this) {
            instance ?: GeckoRuntime.create(
                appContext,
                GeckoRuntimeSettings.Builder()
                    .consoleOutput(BuildConfig.DEBUG)
                    .build(),
            ).also { instance = it }
        }
    }
}
