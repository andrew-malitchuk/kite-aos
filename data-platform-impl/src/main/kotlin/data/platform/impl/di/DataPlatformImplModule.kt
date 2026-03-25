package data.platform.impl.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin DI module for the platform implementation layer.
 *
 * This module provides the concrete Android-specific implementations for interfaces
 * defined in the API module, such as [ConnectivityObserver] and [ApplicationPlatformSource].
 */
@Module
@ComponentScan("data.platform.impl")
public class DataPlatformImplModule
