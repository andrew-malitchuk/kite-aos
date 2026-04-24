package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing the user's progress through the initial setup flow.
 *
 * @property isCompleted `true` if the user has finished onboarding, `false` otherwise.
 *
 * @see Model
 * @since 0.0.1
 */
public data class OnboardingModel(
    val isCompleted: Boolean,
) : Model
