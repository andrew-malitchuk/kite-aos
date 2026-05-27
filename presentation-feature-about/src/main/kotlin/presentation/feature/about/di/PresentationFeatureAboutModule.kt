package presentation.feature.about.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the About feature.
 *
 * This module enables dependency injection for all components within the
 * `presentation.feature.about` package, including the [AboutViewModel].
 *
 * @see AboutViewModel
 * @since 0.0.1
 */

@Module
@ComponentScan("presentation.feature.about")
public class PresentationFeatureAboutModule
