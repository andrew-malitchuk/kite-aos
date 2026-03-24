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
 * The root navigation component of the application.
 *
 * It manages the [NavBackStack], provides the [AppNavigator] via [CompositionLocalProvider],
 * and defines the mapping between [Destination]s and their respective screens.
 *
 * @param startDestination The initial screen to display. Defaults to [Destination.Onboarding].
 */
@Composable
public fun NavigationHost(startDestination: Destination? = null) {
    val backStack = rememberNavBackStack(startDestination ?: Destination.Onboarding)
    val appNavigator = remember(backStack) { AppNavigatorImpl(backStack) }

    CompositionLocalProvider(
        LocalAppNavigator provides appNavigator,
        LocalBackAction provides { appNavigator.popBackStack() },
    ) {
        NavDisplay(
            backStack = backStack,
            entryProvider =
            entryProvider {
                entry<Destination.Onboarding> { OnboardingScreen() }
                entry<Destination.Main> { MainScreen() }
                entry<Destination.Settings> { SettingsScreen() }
                entry<Destination.About> { AboutScreen() }
                entry<Destination.Application> { ApplicationScreen() }
            },
        )
    }
}
