package common.core.analytics.impl.di

import common.core.analytics.api.source.AnalyticsManager
import common.core.analytics.api.source.AnalyticsProvider
import common.core.analytics.impl.source.AnalyticsManagerImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module that registers the [AnalyticsManager] singleton.
 *
 * Resolves all [AnalyticsProvider] instances registered in the Koin graph via
 * `getAll<AnalyticsProvider>()` and injects them into [AnalyticsManagerImpl].
 * The concrete implementation is bound to the [AnalyticsManager] interface so that
 * callers depend only on the API contract.
 *
 * Include this module in the application's composition root after all provider modules
 * (e.g., [common.core.analytics.provider.console.di.CommonCoreAnalyticsProviderConsoleModule])
 * have been registered so that `getAll` picks up every provider.
 *
 * @see AnalyticsManager
 * @see AnalyticsManagerImpl
 * @see AnalyticsProvider
 */
public val analyticsManagerModule: Module = module {
    single { AnalyticsManagerImpl(getAll<AnalyticsProvider>()) } bind AnalyticsManager::class
}
