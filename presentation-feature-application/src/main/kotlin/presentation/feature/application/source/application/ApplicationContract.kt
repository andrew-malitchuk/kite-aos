package presentation.feature.application.source.application

import domain.core.source.model.ApplicationModel

/**
 * Represents the MVI state of the Application Selection screen.
 *
 * This data class is used as the Orbit container state managed by [ApplicationViewModel].
 *
 * @property isLoading Indicates if applications are currently being loaded from the system/database.
 * @property isError Indicates if a technical or logic error occurred during data operations.
 * @property data The list of applications discovered on the device, including their "chosen" status.
 * @see ApplicationViewModel
 * @see ApplicationScreen
 * @since 0.0.1
 */
public data class ApplicationState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val data: List<ApplicationModel> = emptyList(),
)

/**
 * One-off events (side effects) that can occur on the Application Selection screen.
 *
 * Side effects are consumed by [ApplicationScreen] and trigger navigation or
 * UI-only events that cannot be represented in state alone.
 *
 * @see ApplicationViewModel
 * @see ApplicationScreen
 * @since 0.0.1
 */
public sealed class ApplicationSideEffect {
    /** Navigates back to the previous screen. */
    public data object BackClick : ApplicationSideEffect()

    /** Displays an error message using a string resource ID. */
    public data class ShowError(val messageId: Int) : ApplicationSideEffect()
}
