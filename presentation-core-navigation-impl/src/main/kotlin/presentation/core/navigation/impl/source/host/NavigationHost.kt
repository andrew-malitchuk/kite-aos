package presentation.core.navigation.impl.source.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.core.composition.LocalBackAction
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.navigation.impl.source.navigator.AppNavigatorImpl
import presentation.feature.about.source.about.AboutScreen
import presentation.feature.application.source.application.ApplicationScreen
import presentation.feature.main.source.main.MainScreen
import presentation.feature.onboarding.source.onboarding.OnboardingScreen
import presentation.feature.settings.source.settings.SettingsScreen

/**
 * The root navigation host composable for the application.
 *
 * This composable is responsible for:
 * 1. Creating and remembering the [NavBackStack][androidx.navigation3.runtime.NavBackStack].
 * 2. Instantiating the [AppNavigatorImpl] that drives navigation.
 * 3. Providing both [LocalAppNavigator] and [LocalBackAction] via [CompositionLocalProvider]
 *    so that any descendant composable can trigger navigation.
 * 4. Defining the mapping between each [Destination] and its corresponding feature screen.
 *
 * @param startDestination The initial screen to display. When `null`, defaults to
 *   [Destination.Onboarding].
 * @see Destination
 * @see AppNavigatorImpl
 * @see LocalAppNavigator
 * @see LocalBackAction
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun NavigationHost(startDestination: Destination? = null) {
    // Initialise the backstack with the provided start destination, falling back to Onboarding.
    val backStack = rememberNavBackStack(startDestination ?: Destination.Onboarding)

    // The navigator is re-created only when the backstack reference changes.
    val appNavigator = remember(backStack) { AppNavigatorImpl(backStack) }

    // Expose the navigator and a simple back-action lambda to the entire composable subtree.
    CompositionLocalProvider(
        LocalAppNavigator provides appNavigator,
        LocalBackAction provides { appNavigator.popBackStack() },
    ) {
        NavDisplay(
            backStack = backStack,
            entryProvider =
            entryProvider {
                // Each entry maps a Destination type to its feature-level screen composable.
                entry<Destination.Onboarding> { OnboardingScreen() }
                entry<Destination.Main> { MainScreen() }
                entry<Destination.Settings> { SettingsScreen() }
                entry<Destination.About> { AboutScreen() }
                entry<Destination.Application> { ApplicationScreen() }
            },
        )
    }
}
