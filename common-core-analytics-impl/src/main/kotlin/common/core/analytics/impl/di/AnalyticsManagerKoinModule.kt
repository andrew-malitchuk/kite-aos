package common.core.analytics.impl.di

import common.core.analytics.api.source.AnalyticsManager
import common.core.analytics.api.source.AnalyticsProvider
import common.core.analytics.impl.source.AnalyticsManagerImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public val analyticsManagerModule: Module = module {
    single { AnalyticsManagerImpl(getAll<AnalyticsProvider>()) } bind AnalyticsManager::class
}
