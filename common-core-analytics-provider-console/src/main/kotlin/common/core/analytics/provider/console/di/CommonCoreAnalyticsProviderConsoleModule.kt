package common.core.analytics.provider.console.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the console analytics provider.
 *
 * Triggers KSP component scanning across the `common.core.analytics.provider.console`
 * package, which registers [common.core.analytics.provider.console.source.ConsoleAnalyticsProvider]
 * as an [common.core.analytics.api.source.AnalyticsProvider] singleton.
 *
 * Include this module in development or `foss` builds where Firebase is unavailable
 * and analytics should be printed to logcat/stdout instead.
 *
 * @see common.core.analytics.provider.console.source.ConsoleAnalyticsProvider
 */
@Module
@ComponentScan("common.core.analytics.provider.console")
public class CommonCoreAnalyticsProviderConsoleModule
