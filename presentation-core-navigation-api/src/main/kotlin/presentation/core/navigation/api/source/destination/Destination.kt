package presentation.core.navigation.api.source.destination

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Sealed class representing all reachable destinations in the application.
 */
@Serializable
public sealed class Destination : NavKey {
    /** The initial onboarding screen. */
    @Serializable
    public data object Onboarding : Destination()

    /** The main kiosk dashboard screen. */
    @Serializable
    public data object Main : Destination()

    /** Global application settings screen. */
    @Serializable
    public data object Settings : Destination()

    /** The about screen with project information. */
    @Serializable
    public data object About : Destination()

    /** Screen for selecting and launching other applications. */
    @Serializable
    public data object Application : Destination()
}
