package presentation.core.navigation.api.source.destination

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Sealed class representing all reachable destinations in the application.
 *
 * Each concrete data object corresponds to a distinct screen. Because every entry
 * implements [NavKey] and is [Serializable], the destinations can be used directly as
 * type-safe keys in Navigation 3's `NavBackStack` and are safe for state restoration.
 *
 * @see AppNavigator
 * @see presentation.core.navigation.impl.source.host.NavigationHost
 * @since 0.0.1
 */
@Serializable
public sealed class Destination : NavKey {

    /**
     * The initial onboarding screen shown on first launch or when setup is incomplete.
     *
     * @see presentation.feature.onboarding.source.onboarding.OnboardingScreen
     * @since 0.0.1
     */
    @Serializable
    public data object Onboarding : Destination()

    /**
     * The main kiosk dashboard screen displaying the Home Assistant web view.
     *
     * @see presentation.feature.main.source.main.MainScreen
     * @since 0.0.1
     */
    @Serializable
    public data object Main : Destination()

    /**
     * Global application settings screen for configuring kiosk behaviour.
     *
     * @see presentation.feature.settings.source.settings.SettingsScreen
     * @since 0.0.1
     */
    @Serializable
    public data object Settings : Destination()

    /**
     * The about screen displaying project information, version, and attributions.
     *
     * @see presentation.feature.about.source.about.AboutScreen
     * @since 0.0.1
     */
    @Serializable
    public data object About : Destination()

    /**
     * Screen for selecting and launching other installed applications from the kiosk.
     *
     * @see presentation.feature.application.source.application.ApplicationScreen
     * @since 0.0.1
     */
    @Serializable
    public data object Application : Destination()
}
