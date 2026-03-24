package presentation.feature.main.source.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * Entry point for the Main feature (Kiosk Dashboard).
 *
 * It manages the [MainViewModel] state, handles side effects like navigation
 * and application launching, and hosts the primary [MainContent].
 */
@Composable
public fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current

    val snackbarHostState = rememberStackedSnackbarHostState()
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            MainSideEffect.GoToSettingsEffect -> appNavigator?.navigate(Destination.Settings)
            is MainSideEffect.OpenApplicationEffect -> {
                val launchIntent =
                    context.packageManager.getLaunchIntentForPackage(effect.packageName)
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                }
            }

            is MainSideEffect.ShowError -> {
                snackbarHostState.showSnackbar(
                    title = context.getString(effect.messageId),
                )
            }
        }
    }

    MainContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
