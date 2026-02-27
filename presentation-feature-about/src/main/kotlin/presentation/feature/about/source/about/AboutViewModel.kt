package presentation.feature.about.source.about

import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel for the About feature screen.
 *
 * This ViewModel handles user interactions and manages the screen's state and side effects
 * using the Orbit MVI pattern. It coordinates opening external social links and
 * navigating back to previous screens.
 */
@KoinViewModel
public class AboutViewModel : ContainerHost<AboutState, AboutSideEffect>, ViewModel() {

    public companion object {
        /** Link to the GitHub repository. */
        public const val LINK_GITHUB: String = "https://github.com/andrew-malitchuk/kite-aos"
        /** Link to the developer's LinkedIn profile. */
        public const val LINK_LINKEDIN: String = "https://www.linkedin.com/in/andrew-malitchuk"
        /** Link to the developer's Twitter (X) profile. */
        public const val LINK_TWITTER: String = "https://x.com/AndrewMalitchuk"
    }

    override val container: Container<AboutState, AboutSideEffect> = container(AboutState())

    /**
     * Handles opening the GitHub repository link.
     */
    private fun onGitHub() = intent {
        postSideEffect(AboutSideEffect.OpenSocialLinkEffect(LINK_GITHUB))
    }

    /**
     * Handles opening the LinkedIn profile link.
     */
    private fun onLinkedIn() = intent {
        postSideEffect(AboutSideEffect.OpenSocialLinkEffect(LINK_LINKEDIN))
    }

    /**
     * Handles opening the Twitter profile link.
     */
    private fun onTwitter() = intent {
        postSideEffect(AboutSideEffect.OpenSocialLinkEffect(LINK_TWITTER))
    }

    /**
     * Handles the back navigation request.
     */
    private fun onBack() = intent {
        postSideEffect(AboutSideEffect.GoBackEffect)
    }

    /**
     * Main entry point for processing intents (actions) from the UI.
     *
     * @param intent The specific user intent to be processed.
     */
    public fun handleIntent(intent: AboutIntent) {
        when (intent) {
            AboutIntent.OnGitHubIntent -> onGitHub()
            AboutIntent.OnLinkedInIntent -> onLinkedIn()
            AboutIntent.OnTwitterIntent -> onTwitter()
            AboutIntent.OnBackIntent -> onBack()
        }
    }
}
