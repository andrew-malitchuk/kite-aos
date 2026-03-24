package data.repository.impl.source.repository

import data.preferences.api.source.datasource.DashboardPreferenceSource
import data.preferences.api.source.datasource.DockPositionPreferenceSource
import data.preferences.api.source.datasource.LanguagePreferenceSource
import data.preferences.api.source.datasource.MoveDetectorPreferenceSource
import data.preferences.api.source.datasource.OnboardingPreferenceSource
import data.preferences.api.source.datasource.ThemePreferenceSource
import data.repository.impl.core.mapper.DashboardPreferenceMapper
import data.repository.impl.core.mapper.DockPositionPreferenceMapper
import data.repository.impl.core.mapper.LanguagePreferenceMapper
import data.repository.impl.core.mapper.MoveDetectorPreferenceMapper
import data.repository.impl.core.mapper.OnboardingPreferenceMapper
import data.repository.impl.core.mapper.ThemePreferenceMapper
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.core.source.model.OnboardingModel
import domain.core.source.model.ThemeModel
import domain.repository.api.source.repository.ConfigureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Implementation of [ConfigureRepository] that manages various application settings via [PreferenceSource]s.
 *
 * @property themePreferenceProvider Source for theme settings.
 * @property onboardingPreferenceProvider Source for onboarding status.
 * @property dashboardPreferenceSource Source for dashboard configuration.
 * @property dockPositionPreferenceSource Source for dock position.
 * @property moveDetectorPreferenceSource Source for move detector settings.
 * @property languagePreferenceSource Source for application language settings.
 */
@Single(binds = [ConfigureRepository::class])
internal class ConfigureRepositoryImpl(
    private val themePreferenceProvider: ThemePreferenceSource,
    private val onboardingPreferenceProvider: OnboardingPreferenceSource,
    private val dashboardPreferenceSource: DashboardPreferenceSource,
    private val dockPositionPreferenceSource: DockPositionPreferenceSource,
    private val moveDetectorPreferenceSource: MoveDetectorPreferenceSource,
    private val languagePreferenceSource: LanguagePreferenceSource,
) : ConfigureRepository {
    override suspend fun getTheme(): ThemeModel? {
        return themePreferenceProvider.getData()?.let(ThemePreferenceMapper.toModel::map)
    }

    override suspend fun setTheme(theme: ThemeModel?) {
        return themePreferenceProvider.setData(theme?.let(ThemePreferenceMapper.toResource::map))
    }

    override fun observeTheme(): Flow<ThemeModel?> {
        return themePreferenceProvider.observeData().map { it?.let(ThemePreferenceMapper.toModel::map) }
    }

    override suspend fun getOnboarding(): OnboardingModel? {
        return onboardingPreferenceProvider.getData()?.let(OnboardingPreferenceMapper.toModel::map)
    }

    override suspend fun setOnboarding(status: OnboardingModel?) {
        return onboardingPreferenceProvider.setData(status?.let(OnboardingPreferenceMapper.toResource::map))
    }

    override fun observeOnboarding(): Flow<OnboardingModel?> {
        return onboardingPreferenceProvider.observeData()
            .map { it?.let(OnboardingPreferenceMapper.toModel::map) }
    }

    override suspend fun getDashboard(): DashboardModel? {
        return dashboardPreferenceSource.getData()?.let(DashboardPreferenceMapper.toModel::map)
    }

    override suspend fun setDashboard(dashboard: DashboardModel?) {
        return dashboardPreferenceSource.setData(dashboard?.let(DashboardPreferenceMapper.toResource::map))
    }

    override suspend fun getDockPosition(): DockPositionModel? {
        return dockPositionPreferenceSource.getData()?.let(DockPositionPreferenceMapper.toModel::map)
    }

    override suspend fun setDockPosition(dock: DockPositionModel?) {
        return dockPositionPreferenceSource.setData(dock?.let(DockPositionPreferenceMapper.toResource::map))
    }

    override suspend fun getMoveDetector(): MoveDetectorModel? {
        return moveDetectorPreferenceSource.getData()?.let(MoveDetectorPreferenceMapper.toModel::map)
    }

    override suspend fun setMoveDetector(detector: MoveDetectorModel?) {
        return moveDetectorPreferenceSource.setData(detector?.let(MoveDetectorPreferenceMapper.toResource::map))
    }

    override fun observeMoveDetectorMotion(): Flow<Unit> {
        return moveDetectorPreferenceSource.observeMotion()
    }

    override suspend fun emitMoveDetectorMotion() {
        moveDetectorPreferenceSource.emitMotion()
    }

    override suspend fun setApplicationLanguage(localeCode: String) {
        languagePreferenceSource.setData(LanguagePreferenceMapper.toResource.map(localeCode))
    }

    override suspend fun getApplicationLanguage(): String? {
        return languagePreferenceSource.getData()?.let(LanguagePreferenceMapper.toModel::map)
    }
}
