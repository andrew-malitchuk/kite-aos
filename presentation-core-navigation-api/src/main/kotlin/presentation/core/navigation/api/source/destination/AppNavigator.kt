package presentation.core.navigation.api.source.destination

/**
 * Interface for performing navigation actions and managing the backstack.
 */
public interface AppNavigator {
    /**
     * Navigates to a new [destination].
     *
     * @param destination The target destination.
     * @param options Navigation options like singleTop or clearing the backstack.
     */
    public fun navigate(destination: Destination, options: NavOptions = NavOptions.Default)

    /**
     * Removes the top entry from the backstack.
     */
    public fun popBackStack()

    /**
     * Convenience method for backward compatibility or simple back navigation.
     */
    public fun backAction() {
        popBackStack()
    }

    /**
     * Options to control navigation behavior.
     */
    public enum class NavOptions {
        /** Default navigation behavior (adds to backstack). */
        Default,

        /** If the destination is already at the top of the backstack, do nothing. */
        SingleTop,

        /** Clear the entire backstack before navigating. */
        ClearTask,
    }
}
