package presentation.feature.application.source.application

import domain.core.source.model.ApplicationModel

/**
 * Represents the state of the Application Selection screen.
 *
 * @property isLoading Indicates if applications are currently being loaded from the system/database.
 * @property isError Indicates if a technical or logic error occurred during data operations.
 * @property data The list of applications discovered on the device, including their "chosen" status.
 */
public data class ApplicationState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val data: List<ApplicationModel> = emptyList()
)

/**
 * One-off events that can occur on the Application Selection screen.
 */
public sealed class ApplicationSideEffect {
    /** Navigates back to the previous screen. */
    public data object BackClick : ApplicationSideEffect()
    /** Displays an error message using a string resource ID. */
    public data class ShowError(val messageId: Int) : ApplicationSideEffect()
}