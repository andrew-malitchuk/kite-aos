package presentation.feature.about.source.about

/**
 * Represents the UI state for the About screen.
 *
 * This data class is used as the MVI state in the Orbit container managed by [AboutViewModel].
 *
 * @property isLoading Indicates whether the screen is currently performing a background operation.
 * @see AboutViewModel
 * @see AboutScreen
 * @since 0.0.1
 */
public data class AboutState(
    val isLoading: Boolean = false,
)

/**
 * Represents one-off events (side effects) that can occur on the About screen.
 *
 * Side effects are consumed by the [AboutScreen] composable and trigger
 * navigation or external actions that cannot be represented in state alone.
 *
 * @see AboutViewModel
 * @see AboutScreen
 * @since 0.0.1
 */
public sealed class AboutSideEffect {
    /**
     * Triggers the opening of an external social media or project link.
     *
     * @property url The destination URL to be opened.
     */
    public data class OpenSocialLinkEffect(val url: String) : AboutSideEffect()

    /**
     * Triggers a navigation back action to the previous screen.
     */
    public data object GoBackEffect : AboutSideEffect()
}

/**
 * Represents the intents (user actions) that can be dispatched from the About screen.
 *
 * Intents are dispatched from [AboutContent] and processed by [AboutViewModel.handleIntent].
 *
 * @see AboutViewModel
 * @see AboutContent
 * @since 0.0.1
 */
public sealed class AboutIntent {
    /**
     * Intent triggered when the user clicks the back button.
     */
    public data object OnBackIntent : AboutIntent()

    /**
     * Intent triggered when the user clicks the GitHub social link button.
     */
    public data object OnGitHubIntent : AboutIntent()

    /**
     * Intent triggered when the user clicks the LinkedIn social link button.
     */
    public data object OnLinkedInIntent : AboutIntent()

    /**
     * Intent triggered when the user clicks the Twitter (X) social link button.
     */
    public data object OnTwitterIntent : AboutIntent()
}
