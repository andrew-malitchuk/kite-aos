package presentation.feature.main.source.main

import domain.core.source.model.ApplicationModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel

/**
 * Represents the state of the Main (Kiosk) screen.
 *
 * @property dashboardUrls The primary dashboard and whitelist URLs loaded from configuration.
 * @property chosenApps List of applications selected by the user to be visible in the control drawer.
 * @property dockPosition The preferred position of the control drawer (e.g., Bottom or Left).
 * @property isMoveDetectorEnabled Whether camera-based motion detection is active.
 * @property isFabVisible Whether the "Open Drawer" FAB is currently visible (driven by motion).
 * @property fabDelay The duration in seconds the FAB remains visible after the last motion event.
 */
public data class MainState(
    val dashboardUrls: DashboardModel? = null,
    val chosenApps: List<ApplicationModel> = emptyList(),
    val dockPosition: DockPositionModel? = null,
    val isMoveDetectorEnabled: Boolean = false,
    val isFabVisible: Boolean = true,
    val fabDelay: Long = 60L
)

/**
 * One-off side effects for the Main screen.
 */
public sealed class MainSideEffect {
    /** Navigates to the Settings screen. */
    public data object GoToSettingsEffect : MainSideEffect()
    /** Launches an external application by its package name. */
    public data class OpenApplicationEffect(val packageName: String) : MainSideEffect()
    /** Displays an error message via the snackbar system. */
    public data class ShowError(val messageId: Int) : MainSideEffect()
}