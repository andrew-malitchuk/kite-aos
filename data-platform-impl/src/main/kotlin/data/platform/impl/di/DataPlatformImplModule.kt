package data.platform.impl.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin DI module for the platform implementation layer.
 *
 * This module provides the concrete Android-specific implementations for interfaces
 * defined in the API module, such as `ConnectivityObserver` and `ApplicationPlatformSource`.
 * Component scanning is configured to discover all annotated classes under the
 * `data.platform.impl` package.
 *
 * @see data.platform.impl.source.connectivity.ConnectivityObserverImpl
 * @see data.platform.impl.source.datasource.ApplicationPlatformSourceImpl
 * @since 0.0.1
 */
@Module
@ComponentScan("data.platform.impl")
public class DataPlatformImplModule
