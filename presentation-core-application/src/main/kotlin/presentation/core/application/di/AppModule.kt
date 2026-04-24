package presentation.core.application.di

import data.database.impl.di.DataDatabaseImplModule
import data.mqtt.impl.di.DataMqttImplModule
import data.platform.impl.di.DataPlatformImplModule
import data.preferences.impl.di.DataPreferencesImplModule
import data.repository.impl.di.DataRepositoryImplModule
import domain.usecase.impl.di.DomainUseCaseImplModule
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module
import presentation.feature.about.di.PresentationFeatureAboutModule
import presentation.feature.application.di.PresentationFeatureApplicationModule
import presentation.feature.host.di.PresentationFeatureHostModule
import presentation.feature.main.di.PresentationFeatureMainModule
import presentation.feature.onboarding.di.PresentationFeatureOnboardingModule
import presentation.feature.settings.di.PresentationFeatureSettingsModule

/**
 * The main Koin module that acts as the **Composition Root** for the entire application.
 *
 * This module aggregates every layer-specific Koin module (Data, Domain, and Presentation)
 * so that the full dependency graph is available at runtime. Each included module is generated
 * by KSP from its corresponding `@Module @ComponentScan` annotated class.
 *
 * Layer inclusion order:
 * 1. **Data** — database, platform services, preferences, MQTT, and repository bindings.
 * 2. **Domain** — use-case implementations.
 * 3. **Presentation** — feature-level ViewModels and screen-scoped dependencies.
 *
 * @see YahkApplication
 * @see DataDatabaseImplModule
 * @see DataPlatformImplModule
 * @see DataPreferencesImplModule
 * @see DataMqttImplModule
 * @see DataRepositoryImplModule
 * @see DomainUseCaseImplModule
 * @since 0.0.1
 */
public val appModule: Module = module {
    // region Data Layer
    // Database (Room) bindings — DAOs, database instance.
    includes(DataDatabaseImplModule().module)
    // Platform services — device info, sensors, camera access.
    includes(DataPlatformImplModule().module)
    // Proto DataStore preferences.
    includes(DataPreferencesImplModule().module)
    // MQTT client configuration and connection management.
    includes(DataMqttImplModule().module)
    // Repository implementations bridging data sources to domain contracts.
    includes(DataRepositoryImplModule().module)
    // endregion

    // region Domain Layer
    // Use-case implementations that orchestrate business logic.
    includes(DomainUseCaseImplModule().module)
    // endregion

    // region Presentation Layer
    // Feature modules — each provides its ViewModel and screen-scoped dependencies.
    includes(PresentationFeatureMainModule().module)
    includes(PresentationFeatureOnboardingModule().module)
    includes(PresentationFeatureHostModule().module)
    includes(PresentationFeatureSettingsModule().module)
    includes(PresentationFeatureAboutModule().module)
    includes(PresentationFeatureApplicationModule().module)
    // endregion
}
