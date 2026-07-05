package common.core.analytics.provider.firebase.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the Firebase Analytics provider.
 *
 * Triggers KSP component scanning across the `common.core.analytics.provider.firebase`
 * package, which registers [common.core.analytics.provider.firebase.source.FirebaseAnalyticsProvider]
 * as an [common.core.analytics.api.source.AnalyticsProvider] singleton.
 *
 * Only include this module in `gms` builds where Firebase is available. The `foss`
 * build variant should use
 * [common.core.analytics.provider.console.di.CommonCoreAnalyticsProviderConsoleModule] instead.
 *
 * @see common.core.analytics.provider.firebase.source.FirebaseAnalyticsProvider
 */
@Module
@ComponentScan("common.core.analytics.provider.firebase")
public class CommonCoreAnalyticsProviderFirebaseModule
