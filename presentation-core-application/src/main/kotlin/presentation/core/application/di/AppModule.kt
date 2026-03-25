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
 * This module aggregates all other modules from the Data, Domain, and Presentation layers,
 * ensuring that the full dependency graph is available at runtime.
 */
public val appModule: Module = module {
    // region Data Layer
    includes(DataDatabaseImplModule().module)
    includes(DataPlatformImplModule().module)
    includes(DataPreferencesImplModule().module)
    includes(DataMqttImplModule().module)
    includes(DataRepositoryImplModule().module)
    // endregion

    // region Domain Layer
    includes(DomainUseCaseImplModule().module)
    // endregion

    // region Presentation Layer
    includes(PresentationFeatureMainModule().module)
    includes(PresentationFeatureOnboardingModule().module)
    includes(PresentationFeatureHostModule().module)
    includes(PresentationFeatureSettingsModule().module)
    includes(PresentationFeatureAboutModule().module)
    includes(PresentationFeatureApplicationModule().module)
    // endregion
}
