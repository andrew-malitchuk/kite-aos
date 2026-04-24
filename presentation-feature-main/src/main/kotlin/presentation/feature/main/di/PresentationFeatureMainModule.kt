package presentation.feature.main.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the Main (Kiosk) feature.
 *
 * Automatically scans and provides components within the `presentation.feature.main` package,
 * including [MainViewModel].
 *
 * @see MainViewModel
 * @since 0.0.1
 */
@Module
@ComponentScan("presentation.feature.main")
public class PresentationFeatureMainModule
