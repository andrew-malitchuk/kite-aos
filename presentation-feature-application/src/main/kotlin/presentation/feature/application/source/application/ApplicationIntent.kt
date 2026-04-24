package presentation.feature.application.source.application

import domain.core.source.model.ApplicationModel

/**
 * User actions (intents) that can be performed on the Application Selection screen.
 *
 * Intents are dispatched from [ApplicationContent] and processed by
 * [ApplicationViewModel.handleIntent].
 *
 * @see ApplicationViewModel
 * @see ApplicationContent
 * @since 0.0.1
 */
public sealed class ApplicationIntent {
    /** Triggered when the user clicks the back button. */
    public data object OnBackClick : ApplicationIntent()

    /** Manually triggers a reload of the installed applications list. */
    public data object LoadApplications : ApplicationIntent()

    /** Adds an application to the "chosen" list so it appears in the kiosk dashboard. */
    public data class SaveApplication(val applicationModel: ApplicationModel) : ApplicationIntent()

    /** Removes an application from the "chosen" list. */
    public data class RemoveApplication(val applicationModel: ApplicationModel) : ApplicationIntent()
}
