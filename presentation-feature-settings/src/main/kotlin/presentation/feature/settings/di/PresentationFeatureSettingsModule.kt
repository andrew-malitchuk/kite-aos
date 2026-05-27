package presentation.feature.settings.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the settings feature.
 *
 * This module uses component scanning to automatically register ViewModels and other
 * components within the settings feature package.
 *
 * @see SettingsViewModel
 * @since 0.0.1
 */
@Module
@ComponentScan("presentation.feature.settings")
public class PresentationFeatureSettingsModule
