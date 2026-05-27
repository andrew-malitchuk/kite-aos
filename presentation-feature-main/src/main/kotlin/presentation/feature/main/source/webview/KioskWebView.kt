package presentation.feature.main.source.webview

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import domain.core.source.model.WebEngineModel
import presentation.feature.main.source.webview.engine.AndroidWebViewEngine
import presentation.feature.main.source.webview.engine.GeckoViewEngine

/**
 * Creates and remembers a [KioskEngineState] instance across recompositions.
 *
 * @param url The initial URL to load.
 * @param whitelist The list of allowed domains for URL navigation.
 * @return A remembered [KioskEngineState] instance.
 * @since 0.0.4
 */
@Composable
public fun rememberKioskEngineState(
    url: String = "",
    whitelist: List<String> = emptyList(),
): KioskEngineState = remember {
    KioskEngineState(url, whitelist)
}

/**
 * Engine-routing composable for the kiosk browser.
 *
 * Delegates to [AndroidWebViewEngine] or [GeckoViewEngine] based on [engineType].
 * Switching [engineType] at runtime tears down the current engine and creates the new one,
 * which causes a full page reload — this is expected behaviour.
 *
 * @param state The [KioskEngineState] managing URL, whitelist, and navigation state.
 * @param engineType Which browser engine to use.
 * @param modifier Modifier applied to the root container.
 * @see KioskEngineState
 * @since 0.0.4
 */
@Composable
public fun KioskWebView(
    state: KioskEngineState,
    engineType: WebEngineModel,
    modifier: Modifier = Modifier,
) {
    when (engineType) {
        WebEngineModel.AndroidWebView -> AndroidWebViewEngine(state, modifier)
        // GeckoView requires API 26+; fall back to AndroidWebView on older devices
        // so a saved GeckoView preference does not crash on API 25 (gms flavor).
        WebEngineModel.GeckoView -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            GeckoViewEngine(state, modifier)
        } else {
            AndroidWebViewEngine(state, modifier)
        }
    }
}
