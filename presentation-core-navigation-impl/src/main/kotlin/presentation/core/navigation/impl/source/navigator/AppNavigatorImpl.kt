package presentation.core.navigation.impl.source.navigator

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import presentation.core.navigation.api.source.destination.AppNavigator
import presentation.core.navigation.api.source.destination.Destination

/**
 * Concrete implementation of [AppNavigator] backed by Navigation 3's [NavBackStack].
 *
 * This class translates high-level [AppNavigator.NavOptions] into low-level backstack
 * mutations (add / remove).
 *
 * @param backStack The [NavBackStack] instance that holds the navigation entries.
 * @see AppNavigator
 * @see Destination
 * @see presentation.core.navigation.impl.source.host.NavigationHost
 * @since 0.0.1
 */
public class AppNavigatorImpl(
    private val backStack: NavBackStack<NavKey>,
) : AppNavigator {

    /**
     * Removes the top entry from the backstack.
     *
     * Delegates to [NavBackStack.removeLastOrNull] so that an empty backstack does not
     * throw an exception.
     *
     * @see AppNavigator.popBackStack
     * @since 0.0.1
     */
    override fun popBackStack() {
        backStack.removeLastOrNull()
    }

    /**
     * Navigates to the given [destination] according to the specified [options].
     *
     * @param destination The [Destination] to navigate to.
     * @param options The [AppNavigator.NavOptions] that control backstack behaviour.
     * @see AppNavigator.navigate
     * @see AppNavigator.NavOptions
     * @since 0.0.1
     */
    override fun navigate(destination: Destination, options: AppNavigator.NavOptions) {
        when (options) {
            AppNavigator.NavOptions.Default -> backStack.add(destination)

            AppNavigator.NavOptions.SingleTop -> {
                // Only push when the requested destination is not already on top,
                // preventing duplicate entries.
                if (backStack.lastOrNull() != destination) {
                    backStack.add(destination)
                }
            }

            AppNavigator.NavOptions.ClearTask -> {
                // Drain the entire backstack before pushing the new root destination.
                while (backStack.isNotEmpty()) {
                    backStack.removeLastOrNull()
                }
                backStack.add(destination)
            }
        }
    }
}
