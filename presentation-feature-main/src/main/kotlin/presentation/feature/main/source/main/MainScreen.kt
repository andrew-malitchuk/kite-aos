package presentation.feature.main.source.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.feature.main.source.webview.EngineCommand

/**
 * Entry point for the Main feature (Kiosk Dashboard).
 *
 * It manages the [MainViewModel] state, handles side effects like navigation
 * and application launching, and hosts the primary [MainContent].
 *
 * @param viewModel The Koin-provided ViewModel managing the kiosk dashboard logic.
 * @see MainViewModel
 * @see MainContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current

    val snackbarHostState = rememberStackedSnackbarHostState()
    val state = viewModel.collectAsState()
    var openDrawerTrigger by remember { mutableIntStateOf(0) }
    // One channel for every engine instruction, rather than a counter per action: the list grew
    // with each remote command, and each new one meant another parameter threaded into MainContent.
    var engineCommandId by remember { mutableIntStateOf(0) }
    var engineCommand by remember { mutableStateOf<EngineCommand?>(null) }

    viewModel.collectSideEffect { effect ->
        fun send(action: EngineCommand.Action, value: String = "") {
            engineCommandId += 1
            engineCommand = EngineCommand(id = engineCommandId, action = action, value = value)
        }

        when (effect) {
            MainSideEffect.GoToSettingsEffect -> appNavigator?.navigate(Destination.Settings)
            is MainSideEffect.OpenApplicationEffect -> {
                // Fall back to the leanback launch intent for Android TV apps, which have no
                // regular CATEGORY_LAUNCHER entry (getLaunchIntentForPackage returns null).
                val launchIntent =
                    context.packageManager.getLaunchIntentForPackage(effect.packageName)
                        ?: context.packageManager.getLeanbackLaunchIntentForPackage(effect.packageName)
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                }
            }

            is MainSideEffect.ShowError -> {
                snackbarHostState.showSnackbar(
                    title = context.getString(effect.messageId),
                )
            }

            MainSideEffect.OpenDrawerEffect -> openDrawerTrigger++

            MainSideEffect.ReloadWebViewEffect -> send(EngineCommand.Action.RELOAD)
            MainSideEffect.ClearWebViewCacheEffect -> send(EngineCommand.Action.CLEAR_CACHE)
            MainSideEffect.NavigateHomeEffect -> send(EngineCommand.Action.NAVIGATE_HOME)
            MainSideEffect.PauseWebViewEffect -> send(EngineCommand.Action.PAUSE)
            MainSideEffect.ResumeWebViewEffect -> send(EngineCommand.Action.RESUME)
            MainSideEffect.GoBackEffect -> send(EngineCommand.Action.BACK)
            MainSideEffect.GoForwardEffect -> send(EngineCommand.Action.FORWARD)
            is MainSideEffect.NavigateUrlEffect ->
                send(EngineCommand.Action.NAVIGATE, effect.url)

            is MainSideEffect.EvaluateJsEffect ->
                send(EngineCommand.Action.EVALUATE_JS, effect.script)
        }
    }

    MainContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        openDrawerTrigger = openDrawerTrigger,
        engineCommand = engineCommand,
        onUserInteraction = viewModel::onUserInteraction,
    )
}
