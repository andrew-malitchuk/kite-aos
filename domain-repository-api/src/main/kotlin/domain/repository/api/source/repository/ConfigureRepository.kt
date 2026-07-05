package domain.repository.api.source.repository

import domain.core.source.model.AutoRebootModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.HomeAssistantInstanceModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.OnboardingModel
import domain.core.source.model.ScreenStateModel
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.StreamingModel
import domain.core.source.model.ThemeModel
import domain.core.source.model.WebEngineModel
import domain.core.source.model.WebViewRefreshModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing various system and UI configurations.
 *
 * @see data.repository.impl.source.repository.ConfigureRepositoryImpl
 * @since 0.0.1
 */
public interface ConfigureRepository {
    /** Retrieves the current application theme. */
    public suspend fun getTheme(): ThemeModel?

    /** Updates the application [theme]. */
    public suspend fun setTheme(theme: ThemeModel?)

    /** Observes changes to the application theme. */
    public fun observeTheme(): Flow<ThemeModel?>

    /** Retrieves the onboarding completion status. */
    public suspend fun getOnboarding(): OnboardingModel?

    /** Updates the onboarding [status]. */
    public suspend fun setOnboarding(status: OnboardingModel?)

    /** Observes changes to the onboarding status. */
    public fun observeOnboarding(): Flow<OnboardingModel?>

    /** Retrieves the kiosk dashboard configuration. */
    public suspend fun getDashboard(): DashboardModel?

    /** Updates the kiosk [dashboard] configuration. */
    public suspend fun setDashboard(dashboard: DashboardModel?)

    /** Retrieves the current dock position. */
    public suspend fun getDockPosition(): DockPositionModel?

    /** Updates the [dock] position. */
    public suspend fun setDockPosition(dock: DockPositionModel?)

    /** Retrieves the current move detector configuration. */
    public suspend fun getMoveDetector(): MoveDetectorModel?

    /** Updates the move [detector] configuration. */
    public suspend fun setMoveDetector(detector: MoveDetectorModel?)

    /** Observes motion detection events from the move detector. */
    public fun observeMoveDetectorMotion(): Flow<Unit>

    /** Manually triggers a motion detection event. */
    public suspend fun emitMoveDetectorMotion()

    /** Updates the application language using the provided [localeCode] (e.g., "en", "uk"). */
    public suspend fun setApplicationLanguage(localeCode: String)

    /** Retrieves the current application language locale code. */
    public suspend fun getApplicationLanguage(): String?

    /** Retrieves the selected browser engine. */
    public suspend fun getWebEngine(): WebEngineModel?

    /** Updates the selected browser [engine]. */
    public suspend fun setWebEngine(engine: WebEngineModel?)

    /** Retrieves whether auto-return to kiosk is enabled after leaving to an external app. */
    public suspend fun getAutoReturn(): Boolean?

    /** Updates the auto-return kiosk [enabled] setting. */
    public suspend fun setAutoReturn(enabled: Boolean?)

    /**
     * Observes changes to the device's internet connectivity.
     *
     * @return A [Flow] that emits `true` when a network with internet capability is available
     *         and `false` when it is lost. The first emission reflects the current state.
     */
    public fun observeNetworkStatus(): kotlinx.coroutines.flow.Flow<Boolean>

    /** Retrieves the periodic WebView refresh configuration. */
    public suspend fun getWebViewRefresh(): WebViewRefreshModel?

    /** Updates the periodic WebView [refresh] configuration. */
    public suspend fun setWebViewRefresh(refresh: WebViewRefreshModel?)

    /** Retrieves whether reduce motion / disable animations is enabled. */
    public suspend fun getReduceMotion(): Boolean?

    /** Updates the reduce motion [enabled] setting. */
    public suspend fun setReduceMotion(enabled: Boolean?)

    /** Retrieves the current camera streaming configuration. */
    public suspend fun getStreaming(): StreamingModel?

    /** Updates the camera [streaming] configuration. */
    public suspend fun setStreaming(streaming: StreamingModel?)

    /** Observes changes to the camera streaming configuration. */
    public fun observeStreaming(): Flow<StreamingModel?>

    /**
     * Scans the local network for reachable Home Assistant instances.
     *
     * Performs quick mDNS hostname probes first, then falls back to a parallel subnet scan.
     * The total operation is capped at 12 seconds.
     *
     * @return A list of discovered [HomeAssistantInstanceModel] instances. Empty if none are
     *   found or the timeout is exceeded.
     */
    public suspend fun discoverHomeAssistant(): List<HomeAssistantInstanceModel>

    /** Retrieves the current screensaver configuration. */
    public suspend fun getScreensaver(): ScreensaverModel?

    /** Updates the [screensaver] configuration. */
    public suspend fun setScreensaver(screensaver: ScreensaverModel?)

    /** Observes changes to the screensaver configuration. */
    public fun observeScreensaver(): Flow<ScreensaverModel?>

    /** Observes the current screen state (Active / Screensaver). */
    public fun observeScreenState(): Flow<ScreenStateModel>

    /** Emits a screen state update to all observers. */
    public suspend fun emitScreenState(state: ScreenStateModel)

    /** Retrieves the current auto reboot schedule configuration. */
    public suspend fun getAutoReboot(): AutoRebootModel?

    /** Updates the auto reboot schedule [model]. */
    public suspend fun setAutoReboot(model: AutoRebootModel?)

    /** Observes changes to the auto reboot schedule configuration. */
    public fun observeAutoReboot(): Flow<AutoRebootModel?>
}
