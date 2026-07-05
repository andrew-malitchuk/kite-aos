package data.runtime.impl.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin dependency injection module for the runtime data layer implementation.
 *
 * Uses KSP annotation processing to automatically discover and register all injectable
 * components within the `data.runtime.impl` package, including
 * [data.runtime.impl.source.datasource.ScreenStateSourceImpl].
 *
 * @see data.runtime.api.source.datasource.ScreenStateSource
 * @see data.runtime.impl.source.datasource.ScreenStateSourceImpl
 * @since 1.1.0
 */
@Module
@ComponentScan("data.runtime.impl")
public class DataRuntimeImplModule
