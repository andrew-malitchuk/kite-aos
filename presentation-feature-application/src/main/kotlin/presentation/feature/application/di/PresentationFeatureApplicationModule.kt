package presentation.feature.application.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the Application Selection feature.
 *
 * Automatically scans and provides components within the `presentation.feature.application` package,
 * including [ApplicationViewModel].
 *
 * @see ApplicationViewModel
 * @since 0.0.1
 */
@Module
@ComponentScan("presentation.feature.application")
public class PresentationFeatureApplicationModule
