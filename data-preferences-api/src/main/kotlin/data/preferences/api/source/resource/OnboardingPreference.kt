package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing onboarding completion state.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message OnboardingPreference {
 *     optional bool is_completed = 1;
 * }
 * ```
 *
 * @property isCompleted whether the user has completed the initial onboarding flow, or `null` if not yet determined.
 *
 * @see data.preferences.api.source.datasource.OnboardingPreferenceSource
 *
 * @since 0.0.1
 */
public data class OnboardingPreference(
    val isCompleted: Boolean? = null,
) : Resource
