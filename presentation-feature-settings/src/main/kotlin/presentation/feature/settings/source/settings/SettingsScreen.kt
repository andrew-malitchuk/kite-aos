package presentation.feature.settings.source.settings

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.platform.core.extension.openAppLanguageSettings
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * Entry point for the settings feature.
 *
 * This Composable connects the [SettingsViewModel] to the [SettingsContent] and handles
 * side effects like navigation, opening system settings, displaying errors, and
 * triggering application restarts.
 *
 * @param viewModel The Koin-provided ViewModel managing the settings screen logic.
 * @see SettingsViewModel
 * @see SettingsContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current

    val snackbarHostState = rememberStackedSnackbarHostState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SettingsSideEffect.GoBackEffect -> appNavigator?.backAction()
            SettingsSideEffect.OpenLangSettingsEffect -> {
                val wasOpened = context.openAppLanguageSettings()
                viewModel.handleIntent(SettingsIntent.OnLangSystemSettingsResultIntent(wasOpened))
            }
            SettingsSideEffect.GoMoreEffect -> appNavigator?.navigate(Destination.About)
            SettingsSideEffect.GoApplicationEffect -> appNavigator?.navigate(Destination.Application)
            SettingsSideEffect.ShowInAppLanguageSwitcherEffect -> showLanguageDialog = true
            SettingsSideEffect.RestartApplicationEffect -> {
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                val restartIntent = Intent.makeRestartActivityTask(intent?.component)
                context.startActivity(restartIntent)
                Runtime.getRuntime().exit(0)
            }
            is SettingsSideEffect.ShowError -> {
                snackbarHostState.showSnackbar(
                    title = context.getString(effect.message.toInt()),
                )
            }
        }
    }

    SettingsContent(
        state = state,
        onIntent = viewModel::handleIntent,
        showLanguageDialog = showLanguageDialog,
        onShowLanguageDialogChange = { showLanguageDialog = it },
        snackbarHostState = snackbarHostState,
    )
}
