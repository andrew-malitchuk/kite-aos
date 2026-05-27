package data.repository.impl.source.repository

import data.platform.api.source.connectivity.ConnectivityObserver
import data.platform.api.source.scanner.HomeAssistantScanner
import data.preferences.api.source.datasource.DashboardPreferenceSource
import data.preferences.api.source.datasource.DockPositionPreferenceSource
import data.preferences.api.source.datasource.LanguagePreferenceSource
import data.preferences.api.source.datasource.MoveDetectorPreferenceSource
import data.preferences.api.source.datasource.OnboardingPreferenceSource
import data.preferences.api.source.datasource.ThemePreferenceSource
import data.preferences.api.source.datasource.AutoReturnPreferenceSource
import data.preferences.api.source.datasource.WebEnginePreferenceSource
import data.repository.impl.core.mapper.DashboardPreferenceMapper
import data.repository.impl.core.mapper.DockPositionPreferenceMapper
import data.repository.impl.core.mapper.LanguagePreferenceMapper
import data.repository.impl.core.mapper.MoveDetectorPreferenceMapper
import data.repository.impl.core.mapper.OnboardingPreferenceMapper
import data.repository.impl.core.mapper.ThemePreferenceMapper
import data.repository.impl.core.mapper.WebEnginePreferenceMapper
import data.preferences.api.source.resource.AutoReturnPreference
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.HomeAssistantInstanceModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.OnboardingModel
import domain.core.source.model.ThemeModel
import domain.core.source.model.WebEngineModel
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
    private val connectivityObserver: ConnectivityObserver,
    private val homeAssistantScanner: HomeAssistantScanner,
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
    override fun observeNetworkStatus(): Flow<Boolean> =
        connectivityObserver.status.transform { status ->
            emit(
                status == ConnectivityObserver.NetworkStatus.Available ||
                    status == ConnectivityObserver.NetworkStatus.Losing,
            )
        }

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
}
