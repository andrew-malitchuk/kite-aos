package data.repository.impl.source.repository

import data.platform.api.source.connectivity.ConnectivityObserver
import data.platform.api.source.scanner.HomeAssistantScanner
import data.repository.impl.core.mapper.ScreenStateResourceMapper
import data.runtime.api.source.datasource.ScreenStateSource
import data.preferences.api.source.datasource.AutoRebootPreferenceSource
import data.preferences.api.source.datasource.DashboardPreferenceSource
import data.preferences.api.source.datasource.DockPositionPreferenceSource
import data.preferences.api.source.datasource.LanguagePreferenceSource
import data.preferences.api.source.datasource.MoveDetectorPreferenceSource
import data.preferences.api.source.datasource.OnboardingPreferenceSource
import data.preferences.api.source.datasource.ScreensaverPreferenceSource
import data.preferences.api.source.datasource.ThemePreferenceSource
import data.preferences.api.source.datasource.AutoReturnPreferenceSource
import data.preferences.api.source.datasource.StreamingPreferenceSource
import data.preferences.api.source.datasource.WebEnginePreferenceSource
import data.preferences.api.source.datasource.ReduceMotionPreferenceSource
import data.preferences.api.source.datasource.WebViewRefreshPreferenceSource
import data.repository.impl.core.mapper.AutoRebootPreferenceMapper
import data.repository.impl.core.mapper.DashboardPreferenceMapper
import data.repository.impl.core.mapper.DockPositionPreferenceMapper
import data.repository.impl.core.mapper.LanguagePreferenceMapper
import data.repository.impl.core.mapper.MoveDetectorPreferenceMapper
import data.repository.impl.core.mapper.OnboardingPreferenceMapper
import data.repository.impl.core.mapper.ScreensaverPreferenceMapper
import data.repository.impl.core.mapper.ThemePreferenceMapper
import data.repository.impl.core.mapper.StreamingPreferenceMapper
import data.repository.impl.core.mapper.WebEnginePreferenceMapper
import data.repository.impl.core.mapper.WebViewRefreshPreferenceMapper
import data.preferences.api.source.resource.AutoReturnPreference
import data.preferences.api.source.resource.ReduceMotionPreference
import domain.core.source.model.AutoRebootModel
import domain.core.source.model.ScreenStateModel
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.StreamingModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.HomeAssistantInstanceModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.OnboardingModel
import domain.core.source.model.ThemeModel
import domain.core.source.model.WebEngineModel
import domain.core.source.model.WebViewRefreshModel
import domain.repository.api.source.repository.ConfigureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.koin.core.annotation.Single

/**
 * Implementation of [ConfigureRepository] that manages various application settings
 * via dedicated preference sources.
 *
 * This repository aggregates multiple preference data sources to provide a unified
 * configuration API for the domain layer, covering theme, onboarding, dashboard,
 * dock position, motion detection, and language settings.
 *
 * @property themePreferenceProvider Source for theme settings.
 * @property onboardingPreferenceProvider Source for onboarding completion status.
 * @property dashboardPreferenceSource Source for dashboard URL configuration.
 * @property dockPositionPreferenceSource Source for dock position settings.
 * @property moveDetectorPreferenceSource Source for motion detector settings and events.
 * @property languagePreferenceSource Source for application language/locale settings.
 * @property webEnginePreferenceSource Source for browser engine selection.
 * @see ConfigureRepository
 * @see ThemePreferenceMapper
 * @see OnboardingPreferenceMapper
 * @see DashboardPreferenceMapper
 * @see DockPositionPreferenceMapper
 * @see MoveDetectorPreferenceMapper
 * @see LanguagePreferenceMapper
 * @see WebEnginePreferenceMapper
 * @since 0.0.1
 */
@Single(binds = [ConfigureRepository::class])
internal class ConfigureRepositoryImpl(
    private val themePreferenceProvider: ThemePreferenceSource,
    private val onboardingPreferenceProvider: OnboardingPreferenceSource,
    private val dashboardPreferenceSource: DashboardPreferenceSource,
    private val dockPositionPreferenceSource: DockPositionPreferenceSource,
    private val moveDetectorPreferenceSource: MoveDetectorPreferenceSource,
    private val languagePreferenceSource: LanguagePreferenceSource,
    private val webEnginePreferenceSource: WebEnginePreferenceSource,
    private val autoReturnPreferenceSource: AutoReturnPreferenceSource,
    private val webViewRefreshPreferenceSource: WebViewRefreshPreferenceSource,
    private val reduceMotionPreferenceSource: ReduceMotionPreferenceSource,
    private val streamingPreferenceSource: StreamingPreferenceSource,
    private val screensaverPreferenceSource: ScreensaverPreferenceSource,
    private val autoRebootPreferenceSource: AutoRebootPreferenceSource,
    private val connectivityObserver: ConnectivityObserver,
    private val homeAssistantScanner: HomeAssistantScanner,
    private val screenStateSource: ScreenStateSource,
) : ConfigureRepository {

    /**
     * Retrieves the current theme setting.
     *
     * @return the current [ThemeModel], or `null` if not yet configured.
     */
    override suspend fun getTheme(): ThemeModel? {
        return themePreferenceProvider.getData()?.let(ThemePreferenceMapper.toModel::map)
    }

    /**
     * Persists the given theme setting.
     *
     * @param theme the [ThemeModel] to store, or `null` to clear.
     */
    override suspend fun setTheme(theme: ThemeModel?) {
        return themePreferenceProvider.setData(theme?.let(ThemePreferenceMapper.toResource::map))
    }

    /**
     * Observes changes to the theme setting.
     *
     * @return a [Flow] emitting the current [ThemeModel] whenever it changes.
     */
    override fun observeTheme(): Flow<ThemeModel?> {
        return themePreferenceProvider.observeData().map { it?.let(ThemePreferenceMapper.toModel::map) }
    }

    /**
     * Retrieves the current onboarding completion status.
     *
     * @return the current [OnboardingModel], or `null` if not yet configured.
     */
    override suspend fun getOnboarding(): OnboardingModel? {
        return onboardingPreferenceProvider.getData()?.let(OnboardingPreferenceMapper.toModel::map)
    }

    /**
     * Persists the given onboarding status.
     *
     * @param status the [OnboardingModel] to store, or `null` to clear.
     */
    override suspend fun setOnboarding(status: OnboardingModel?) {
        return onboardingPreferenceProvider.setData(status?.let(OnboardingPreferenceMapper.toResource::map))
    }

    /**
     * Observes changes to the onboarding completion status.
     *
     * @return a [Flow] emitting the current [OnboardingModel] whenever it changes.
     */
    override fun observeOnboarding(): Flow<OnboardingModel?> {
        return onboardingPreferenceProvider.observeData()
            .map { it?.let(OnboardingPreferenceMapper.toModel::map) }
    }

    /**
     * Retrieves the current dashboard configuration.
     *
     * @return the current [DashboardModel], or `null` if not yet configured.
     */
    override suspend fun getDashboard(): DashboardModel? {
        return dashboardPreferenceSource.getData()?.let(DashboardPreferenceMapper.toModel::map)
    }

    /**
     * Persists the given dashboard configuration.
     *
     * @param dashboard the [DashboardModel] to store, or `null` to clear.
     */
    override suspend fun setDashboard(dashboard: DashboardModel?) {
        return dashboardPreferenceSource.setData(dashboard?.let(DashboardPreferenceMapper.toResource::map))
    }

    /**
     * Retrieves the current dock position setting.
     *
     * @return the current [DockPositionModel], or `null` if not yet configured.
     */
    override suspend fun getDockPosition(): DockPositionModel? {
        return dockPositionPreferenceSource.getData()?.let(DockPositionPreferenceMapper.toModel::map)
    }

    /**
     * Persists the given dock position setting.
     *
     * @param dock the [DockPositionModel] to store, or `null` to clear.
     */
    override suspend fun setDockPosition(dock: DockPositionModel?) {
        return dockPositionPreferenceSource.setData(dock?.let(DockPositionPreferenceMapper.toResource::map))
    }

    /**
     * Retrieves the current motion detector configuration.
     *
     * @return the current [MoveDetectorModel], or `null` if not yet configured.
     */
    override suspend fun getMoveDetector(): MoveDetectorModel? {
        return moveDetectorPreferenceSource.getData()?.let(MoveDetectorPreferenceMapper.toModel::map)
    }

    /**
     * Persists the given motion detector configuration.
     *
     * @param detector the [MoveDetectorModel] to store, or `null` to clear.
     */
    override suspend fun setMoveDetector(detector: MoveDetectorModel?) {
        return moveDetectorPreferenceSource.setData(detector?.let(MoveDetectorPreferenceMapper.toResource::map))
    }

    /**
     * Observes motion detection events from the move detector.
     *
     * @return a [Flow] that emits [Unit] each time a motion event is detected.
     */
    override fun observeMoveDetectorMotion(): Flow<Unit> {
        return moveDetectorPreferenceSource.observeMotion()
    }

    /**
     * Emits a motion detection event to notify observers.
     */
    override suspend fun emitMoveDetectorMotion() {
        moveDetectorPreferenceSource.emitMotion()
    }

    /**
     * Persists the application language locale code.
     *
     * @param localeCode the locale code to store (e.g., "en", "uk").
     */
    override suspend fun setApplicationLanguage(localeCode: String) {
        languagePreferenceSource.setData(LanguagePreferenceMapper.toResource.map(localeCode))
    }

    /**
     * Retrieves the current application language locale code.
     *
     * @return the stored locale code string, or `null` if not yet configured.
     */
    override suspend fun getApplicationLanguage(): String? {
        return languagePreferenceSource.getData()?.let(LanguagePreferenceMapper.toModel::map)
    }

    /**
     * Retrieves the selected browser engine.
     *
     * @return the current [WebEngineModel], or `null` if not yet configured.
     */
    override suspend fun getWebEngine(): WebEngineModel? {
        return webEnginePreferenceSource.getData()?.let(WebEnginePreferenceMapper.toModel::map)
    }

    /**
     * Persists the selected browser engine.
     *
     * @param engine the [WebEngineModel] to store, or `null` to clear.
     */
    override suspend fun setWebEngine(engine: WebEngineModel?) {
        return webEnginePreferenceSource.setData(engine?.let(WebEnginePreferenceMapper.toResource::map))
    }

    /**
     * Retrieves whether auto-return to kiosk is enabled.
     *
     * @return `true` if enabled, `false` if disabled, or `null` if not yet configured.
     */
    override suspend fun getAutoReturn(): Boolean? {
        return autoReturnPreferenceSource.getData()?.isEnabled
    }

    /**
     * Persists the auto-return kiosk setting.
     *
     * @param enabled `true` to enable auto-return, `false` to disable, or `null` to clear.
     */
    override suspend fun setAutoReturn(enabled: Boolean?) {
        autoReturnPreferenceSource.setData(enabled?.let { AutoReturnPreference(isEnabled = it) })
    }

    /**
     * Observes device internet connectivity via [ConnectivityObserver].
     *
     * @return a [Flow] emitting `true` when network is available, `false` when lost/unavailable.
     */
    override fun observeNetworkStatus(): Flow<Boolean> = connectivityObserver.status.transform { status ->
        emit(
            status == ConnectivityObserver.NetworkStatus.Available ||
                status == ConnectivityObserver.NetworkStatus.Losing,
        )
    }

    /**
     * Retrieves the periodic WebView refresh configuration.
     *
     * @return the current [WebViewRefreshModel], or `null` if not yet configured.
     */
    override suspend fun getWebViewRefresh(): WebViewRefreshModel? {
        return webViewRefreshPreferenceSource.getData()?.let(WebViewRefreshPreferenceMapper.toModel::map)
    }

    /**
     * Persists the periodic WebView refresh configuration.
     *
     * @param refresh the [WebViewRefreshModel] to store, or `null` to clear.
     */
    override suspend fun setWebViewRefresh(refresh: WebViewRefreshModel?) {
        return webViewRefreshPreferenceSource.setData(refresh?.let(WebViewRefreshPreferenceMapper.toResource::map))
    }

    /**
     * Retrieves whether reduce motion / disable animations is enabled.
     *
     * @return `true` if reduce motion is enabled, `false` if disabled, or `null` if not yet configured.
     */
    override suspend fun getReduceMotion(): Boolean? {
        return reduceMotionPreferenceSource.getData()?.isEnabled
    }

    /**
     * Persists the reduce motion setting.
     *
     * @param enabled `true` to enable reduce motion, `false` to disable, or `null` to clear.
     */
    override suspend fun setReduceMotion(enabled: Boolean?) {
        reduceMotionPreferenceSource.setData(enabled?.let { ReduceMotionPreference(isEnabled = it) })
    }

    /**
     * Retrieves the current MJPEG camera streaming configuration.
     *
     * @return the current [StreamingModel], or `null` if not yet configured.
     */
    override suspend fun getStreaming(): StreamingModel? {
        return streamingPreferenceSource.getData()?.let(StreamingPreferenceMapper.toModel::map)
    }

    /**
     * Persists the MJPEG camera streaming configuration.
     *
     * @param streaming the [StreamingModel] to store, or `null` to clear.
     */
    override suspend fun setStreaming(streaming: StreamingModel?) {
        return streamingPreferenceSource.setData(streaming?.let(StreamingPreferenceMapper.toResource::map))
    }

    /**
     * Observes changes to the MJPEG camera streaming configuration.
     *
     * @return a [kotlinx.coroutines.flow.Flow] emitting the current [StreamingModel] whenever it changes.
     */
    override fun observeStreaming(): kotlinx.coroutines.flow.Flow<StreamingModel?> {
        return streamingPreferenceSource.observeData().map { it?.let(StreamingPreferenceMapper.toModel::map) }
    }

    /**
     * Scans the local network for reachable Home Assistant instances via [homeAssistantScanner].
     *
     * Performs a quick mDNS hostname probe first, then falls back to a parallel subnet scan.
     * The source string `"quick"` maps to [HomeAssistantInstanceModel.DiscoverySource.Quick];
     * any other value maps to [HomeAssistantInstanceModel.DiscoverySource.Scan].
     *
     * @return a list of discovered [HomeAssistantInstanceModel] instances, empty if none are found.
     */
    override suspend fun discoverHomeAssistant(): List<HomeAssistantInstanceModel> =
        homeAssistantScanner.discover().map { host ->
            HomeAssistantInstanceModel(
                url = host.url,
                source = when (host.source) {
                    "quick" -> HomeAssistantInstanceModel.DiscoverySource.Quick
                    else -> HomeAssistantInstanceModel.DiscoverySource.Scan
                },
            )
        }

    /**
     * Retrieves the current screensaver configuration.
     *
     * @return the current [ScreensaverModel], or `null` if not yet configured.
     */
    override suspend fun getScreensaver(): ScreensaverModel? {
        return screensaverPreferenceSource.getData()?.let(ScreensaverPreferenceMapper.toModel::map)
    }

    /**
     * Persists the screensaver configuration.
     *
     * @param screensaver the [ScreensaverModel] to store, or `null` to clear.
     */
    override suspend fun setScreensaver(screensaver: ScreensaverModel?) {
        return screensaverPreferenceSource.setData(screensaver?.let(ScreensaverPreferenceMapper.toResource::map))
    }

    /**
     * Observes changes to the screensaver configuration.
     *
     * @return a [Flow] emitting the current [ScreensaverModel] whenever it changes.
     */
    override fun observeScreensaver(): Flow<ScreensaverModel?> {
        return screensaverPreferenceSource.observeData().map { it?.let(ScreensaverPreferenceMapper.toModel::map) }
    }

    /**
     * Observes the current kiosk screen state via [screenStateSource].
     *
     * @return a [Flow] emitting [ScreenStateModel.Active] or [ScreenStateModel.Screensaver]
     *   whenever the screen state changes.
     */
    override fun observeScreenState(): Flow<ScreenStateModel> =
        screenStateSource.observe().map(ScreenStateResourceMapper.toModel::map)

    /**
     * Emits a screen state update to all active observers.
     *
     * @param state the new [ScreenStateModel] to broadcast.
     */
    override suspend fun emitScreenState(state: ScreenStateModel) {
        screenStateSource.emit(ScreenStateResourceMapper.toResource.map(state))
    }

    /**
     * Retrieves the current auto-reboot schedule configuration.
     *
     * @return the current [AutoRebootModel], or `null` if not yet configured.
     */
    override suspend fun getAutoReboot(): AutoRebootModel? {
        return autoRebootPreferenceSource.getData()?.let(AutoRebootPreferenceMapper.toModel::map)
    }

    /**
     * Persists the auto-reboot schedule configuration.
     *
     * @param model the [AutoRebootModel] to store, or `null` to clear.
     */
    override suspend fun setAutoReboot(model: AutoRebootModel?) {
        autoRebootPreferenceSource.setData(model?.let(AutoRebootPreferenceMapper.toResource::map))
    }

    /**
     * Observes changes to the auto-reboot schedule configuration.
     *
     * @return a [Flow] emitting the current [AutoRebootModel] whenever it changes.
     */
    override fun observeAutoReboot(): Flow<AutoRebootModel?> {
        return autoRebootPreferenceSource.observeData().map { it?.let(AutoRebootPreferenceMapper.toModel::map) }
    }
}
