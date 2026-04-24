package presentation.feature.host.source.host

import domain.core.source.model.ThemeModel
import presentation.core.navigation.api.source.destination.Destination

/**
 * Represents the MVI state for the application's host activity.
 *
 * This data class is used as the Orbit container state managed by [HostViewModel].
 *
 * @property startDestination The initial navigation destination (Main or Onboarding)
 *   determined during bootstrapping. Null if not yet decided.
 * @property theme The current application theme observed from user preferences.
 * @see HostViewModel
 * @see HostActivity
 * @since 0.0.1
 */
public data class HostState(
    val startDestination: Destination? = null,
    val theme: ThemeModel = ThemeModel.Light,
)

/**
 * Side effects for the host activity, primarily used for lifecycle-dependent
 * UI transitions like dismissing the splash screen.
 *
 * @see HostViewModel
 * @see HostActivity
 * @since 0.0.1
 */
public sealed class HostSideEffect {
    /**
     * Triggered when the initial bootstrapping is complete and the splash
     * screen should be dismissed.
     */
    public data object DismissSplashEffect : HostSideEffect()
}

/**
 * User intents for the host activity.
 *
 * @see HostViewModel
 * @see HostActivity
 * @since 0.0.1
 */
public sealed class HostIntent {
    /**
     * Intent triggered when the application starts and needs to determine
     * its initial state and navigation path.
     */
    public data object OnStartIntent : HostIntent()
}
