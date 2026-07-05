package presentation.core.application.di

import common.core.analytics.provider.console.di.CommonCoreAnalyticsProviderConsoleModule
import org.koin.core.module.Module
import org.koin.ksp.generated.module

/**
 * Registers analytics providers for the `foss` build flavor.
 *
 * In the `foss` variant, only the console provider is active because Firebase Analytics
 * is not available. Registers [CommonCoreAnalyticsProviderConsoleModule] so that events
 * are printed to stdout/logcat instead of being forwarded to Firebase.
 *
 * @see CommonCoreAnalyticsProviderConsoleModule
 * @see presentation.core.application.di.AppModule
 */
internal fun Module.registerAnalyticsProviders() {
    includes(CommonCoreAnalyticsProviderConsoleModule().module)
}
