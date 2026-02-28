package presentation.feature.application.di

import org.koin.core.annotation.Module
import org.koin.core.annotation.ComponentScan

/**
 * Koin module for the Application Selection feature.
 *
 * Automatically scans and provides components within the 'presentation.feature.application' package,
 * including [ApplicationViewModel].
 */
@Module
@ComponentScan("presentation.feature.application")
public class PresentationFeatureApplicationModule