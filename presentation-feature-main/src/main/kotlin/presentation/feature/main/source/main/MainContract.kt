package presentation.feature.main.source.main

import domain.core.source.model.ApplicationModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.ScreensaverSource
import domain.core.source.model.WebEngineModel

/**
 * Represents the MVI state of the Main (Kiosk) screen.
 *
 * This data class is used as the Orbit container state managed by [MainViewModel].
 *
 * @property dashboardUrls The primary dashboard and whitelist URLs loaded from configuration.
 * @property chosenApps List of applications selected by the user to be visible in the control drawer.
 * @property dockPosition The preferred position of the control drawer (e.g., Bottom or Left).
 * @property isMoveDetectorEnabled Whether camera-based motion detection is active.
 * @property isFabVisible Whether the "Open Drawer" FAB is currently visible (driven by motion).
 * @property fabDelay The duration in seconds the FAB remains visible after the last motion event.
 * @property isScreensaverVisible Whether the screensaver overlay is currently shown.
 * @property screensaverShowClock Whether the clock is displayed on the screensaver overlay.
 * @property screensaverFolderUri URI of the local image folder used for slideshow mode.
 * @property screensaverSlideInterval Seconds between image transitions in slideshow mode.
 * @property screensaverSource The screensaver background source type.
 * @see MainViewModel
 * @see MainScreen
 * @since 0.0.1
 */
public data class MainState(
    val dashboardUrls: DashboardModel? = null,
    val chosenApps: List<ApplicationModel> = emptyList(),
    val dockPosition: DockPositionModel? = null,
    val isMoveDetectorEnabled: Boolean = false,
    val isFabVisible: Boolean = true,
    val fabDelay: Long = 60L,
    val webEngine: WebEngineModel = WebEngineModel.AndroidWebView,
    /** Whether the device currently has internet connectivity. */
    val isNetworkAvailable: Boolean = true,
    val isWebViewRefreshEnabled: Boolean = false,
    val webViewRefreshInterval: Long = 300L,
    val isStreamingEnabled: Boolean = false,
    val isScreensaverVisible: Boolean = false,
    val screensaverShowClock: Boolean = true,
    val screensaverFolderUri: String? = null,
    val screensaverSlideInterval: Long = 30L,
    val screensaverSource: ScreensaverSource = ScreensaverSource.BLACK,
)

/**
 * One-off side effects for the Main screen.
 *
 * Side effects are consumed by [MainScreen] and trigger navigation,
 * application launching, or error display events.
 *
 * @see MainViewModel
 * @see MainScreen
 * @since 0.0.1
 */
public sealed class MainSideEffect {
    /** Navigates to the Settings screen. */
    public data object GoToSettingsEffect : MainSideEffect()

    /** Launches an external application by its package name. */
    public data class OpenApplicationEffect(val packageName: String) : MainSideEffect()

    /** Displays an error message via the snackbar system. */
    public data class ShowError(val messageId: Int) : MainSideEffect()

    /** Signals the WebView to perform a full page reload. */
    public data object ReloadWebViewEffect : MainSideEffect()
}
