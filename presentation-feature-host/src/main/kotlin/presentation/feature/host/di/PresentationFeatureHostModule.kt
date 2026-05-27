package presentation.feature.host.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Dependency Injection module for the host feature.
 *
 * This module enables dependency injection for all components within the
 * `presentation.feature.host` package, specifically providing the [HostViewModel].
 *
 * @see HostViewModel
 * @since 0.0.1
 */
@Module
@ComponentScan("presentation.feature.host")
public class PresentationFeatureHostModule
