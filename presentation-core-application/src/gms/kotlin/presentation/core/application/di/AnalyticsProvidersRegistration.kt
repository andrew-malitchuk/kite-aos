package presentation.core.application.di

import common.core.analytics.provider.console.di.CommonCoreAnalyticsProviderConsoleModule
import common.core.analytics.provider.firebase.di.CommonCoreAnalyticsProviderFirebaseModule
import org.koin.core.module.Module
import org.koin.ksp.generated.module

/**
 * Registers analytics providers for the `gms` build flavor.
 *
 * In the `gms` variant, both the console provider and the Firebase Analytics provider
 * are active. Events are printed to stdout/logcat via [CommonCoreAnalyticsProviderConsoleModule]
 * and simultaneously forwarded to Firebase via [CommonCoreAnalyticsProviderFirebaseModule].
 *
 * @see CommonCoreAnalyticsProviderConsoleModule
 * @see CommonCoreAnalyticsProviderFirebaseModule
 * @see presentation.core.application.di.AppModule
 */
internal fun Module.registerAnalyticsProviders() {
    includes(CommonCoreAnalyticsProviderConsoleModule().module)
    includes(CommonCoreAnalyticsProviderFirebaseModule().module)
}
