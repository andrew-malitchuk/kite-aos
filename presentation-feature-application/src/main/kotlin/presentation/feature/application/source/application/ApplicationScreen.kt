package presentation.feature.application.source.application

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * Entry point for the Application Selection feature.
 *
 * This screen provides a list of installed applications and allows the user
 * to select which ones should be available in the kiosk dashboard.
 */
@Composable
public fun ApplicationScreen(
    viewModel: ApplicationViewModel = koinViewModel(),
) {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current
    val snackbarHostState = rememberStackedSnackbarHostState()
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            ApplicationSideEffect.BackClick -> appNavigator?.backAction()
            is ApplicationSideEffect.ShowError -> {
                snackbarHostState.showSnackbar(
                    title = context.getString(effect.messageId)
                )
            }
        }
    }

    ApplicationContent(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState
    )
}
