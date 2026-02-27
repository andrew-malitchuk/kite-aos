package presentation.feature.about.di

import org.koin.core.annotation.Module
import org.koin.core.annotation.ComponentScan

/**

 * Koin module for the About feature.

 *

 * This module enables dependency injection for all components within the

 * 'presentation.feature.about' package, including the [AboutViewModel].

 */

@Module

@ComponentScan("presentation.feature.about")

public class PresentationFeatureAboutModule
