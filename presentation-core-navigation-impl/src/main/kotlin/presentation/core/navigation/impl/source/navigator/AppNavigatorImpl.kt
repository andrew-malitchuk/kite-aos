package presentation.core.navigation.impl.source.navigator

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import presentation.core.navigation.api.source.destination.AppNavigator
import presentation.core.navigation.api.source.destination.Destination


/**
 * Concrete implementation of [AppNavigator] using Navigation 3's [NavBackStack].
 *
 * @property backStack The backstack to manage navigation entries.
 */
public class AppNavigatorImpl(
    private val backStack: NavBackStack<NavKey>
) : AppNavigator {

    override fun popBackStack() {
        backStack.removeLastOrNull()
    }

    override fun navigate(destination: Destination, options: AppNavigator.NavOptions) {
        when (options) {
            AppNavigator.NavOptions.Default -> backStack.add(destination)
            AppNavigator.NavOptions.SingleTop -> {
                if (backStack.lastOrNull() != destination) {
                    backStack.add(destination)
                }
            }
            AppNavigator.NavOptions.ClearTask -> {
                while (backStack.isNotEmpty()) {
                    backStack.removeLastOrNull()
                }
                backStack.add(destination)
            }
        }
    }
}