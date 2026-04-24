package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.OnboardingPreference

/**
 * Preference data source for onboarding completion state.
 *
 * Manages persistence and observation of whether the user has completed
 * the initial onboarding flow.
 *
 * @see OnboardingPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface OnboardingPreferenceSource : PreferenceSource<OnboardingPreference>
