package presentation.feature.about.source.about

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator

/**
 * Entry point for the About feature screen.
 *
 * This Composable connects the [AboutViewModel] with the UI, observing state
 * changes and collecting side effects for navigation and external link handling.
 * It delegates rendering to [AboutContent] and handles [AboutSideEffect] events
 * such as opening social links in the browser and navigating back.
 *
 * @param viewModel The Koin-provided ViewModel managing the About screen logic.
 * @see AboutViewModel
 * @see AboutContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun AboutScreen(viewModel: AboutViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is AboutSideEffect.OpenSocialLinkEffect -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url))
                context.startActivity(intent)
            }

            AboutSideEffect.GoBackEffect -> appNavigator?.backAction()
        }
    }

    AboutContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}
