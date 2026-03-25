package presentation.feature.onboarding.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the onboarding feature.
 *
 * It uses the 'koin-annotations' library to automatically scan and provide components
 * (like [OnboardingViewModel]) within this package.
 */
@Module
@ComponentScan("presentation.feature.onboarding")
public class PresentationFeatureOnboardingModule
