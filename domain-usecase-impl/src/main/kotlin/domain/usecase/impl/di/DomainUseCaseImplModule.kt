package domain.usecase.impl.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the domain use case implementations.
 *
 * This module uses component scanning to automatically discover and provide
 * use case singletons.
 *
 * @since 0.0.1
 */
@Module
@ComponentScan("domain.usecase.impl")
public class DomainUseCaseImplModule
