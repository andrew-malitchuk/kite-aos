package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.OnboardingPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.OnboardingModel

/**
 * Mapper for converting between [OnboardingModel] and [OnboardingPreference].
 *
 * Handles bidirectional mapping between the domain representation of onboarding completion
 * status and the preference storage resource. When converting from [OnboardingPreference]
 * to [OnboardingModel], a null [OnboardingPreference.isCompleted] value defaults to `false`.
 *
 * @see OnboardingModel
 * @see OnboardingPreference
 * @see ModelResourceMapper
 * @since 0.0.1
 */
internal object OnboardingPreferenceMapper :
    ModelResourceMapper<OnboardingModel, OnboardingPreference> {

    override val toModel: Mapper<OnboardingPreference, OnboardingModel> =
        Mapper { input ->
            OnboardingModel(
                // Default to false when the preference value is null (not yet set)
                isCompleted = input.isCompleted ?: false,
            )
        }

    override val toResource: Mapper<OnboardingModel, OnboardingPreference> =
        Mapper { input ->
            OnboardingPreference(
                isCompleted = input.isCompleted,
            )
        }
}
