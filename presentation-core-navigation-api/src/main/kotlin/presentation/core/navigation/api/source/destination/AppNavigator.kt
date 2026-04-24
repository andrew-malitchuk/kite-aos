package presentation.core.navigation.api.source.destination

/**
 * Contract for performing navigation actions and managing the backstack.
 *
 * Implementations of this interface bridge the application's [Destination] model with the
 * underlying navigation framework (e.g., Navigation 3's `NavBackStack`).
 *
 * @see Destination
 * @see presentation.core.navigation.impl.source.navigator.AppNavigatorImpl
 * @see presentation.core.navigation.api.core.composition.LocalAppNavigator
 * @since 0.0.1
 */
public interface AppNavigator {

    /**
     * Navigates to the specified [destination].
     *
     * The behaviour of the navigation can be customised via [options], which controls
     * whether the destination replaces the current top, clears the backstack, or simply
     * pushes a new entry.
     *
     * @param destination The target [Destination] to navigate to.
     * @param options [NavOptions] controlling backstack behaviour. Defaults to [NavOptions.Default].
     * @see Destination
     * @see NavOptions
     * @since 0.0.1
     */
    public fun navigate(destination: Destination, options: NavOptions = NavOptions.Default)

    /**
     * Removes the top entry from the backstack, effectively navigating backward.
     *
     * If the backstack is empty, the behaviour is implementation-defined.
     *
     * @see backAction
     * @see presentation.core.navigation.api.core.composition.LocalBackAction
     * @since 0.0.1
     */
    public fun popBackStack()

    /**
     * Convenience method for backward-compatible or simple back navigation.
     *
     * Delegates to [popBackStack] by default. Consumers that only need a "go back" callback
     * (e.g., toolbar up buttons) can reference this method directly.
     *
     * @see popBackStack
     * @since 0.0.1
     */
    public fun backAction() {
        popBackStack()
    }

    /**
     * Options that control how a navigation action interacts with the backstack.
     *
     * @see AppNavigator.navigate
     * @since 0.0.1
     */
    public enum class NavOptions {
        /**
         * Default navigation behaviour — the destination is simply added to the backstack.
         *
         * @since 0.0.1
         */
        Default,

        /**
         * If the destination is already at the top of the backstack, the navigation is
         * a no-op. Otherwise, the destination is pushed onto the backstack as usual.
         *
         * @since 0.0.1
         */
        SingleTop,

        /**
         * Clear the entire backstack before navigating to the destination, making it the
         * new root of the navigation graph.
         *
         * @since 0.0.1
         */
        ClearTask,
    }
}
