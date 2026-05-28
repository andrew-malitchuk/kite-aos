package presentation.core.application.di

import common.core.analytics.provider.console.di.CommonCoreAnalyticsProviderConsoleModule
import org.koin.core.module.Module
import org.koin.ksp.generated.module

internal fun Module.registerAnalyticsProviders() {
    includes(CommonCoreAnalyticsProviderConsoleModule().module)
}
