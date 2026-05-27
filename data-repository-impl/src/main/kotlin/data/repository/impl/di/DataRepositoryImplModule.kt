package data.repository.impl.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin dependency injection module for the data repository implementation layer.
 *
 * Scans the `data.repository.impl` package tree for annotated components (e.g., repository
 * implementations marked with `@Single`) and registers them in the Koin dependency graph.
 * This module is included in the application's composition root.
 *
 * @see org.koin.core.annotation.Module
 * @see org.koin.core.annotation.ComponentScan
 * @since 0.0.1
 */
@Module
@ComponentScan("data.repository.impl")
public class DataRepositoryImplModule
