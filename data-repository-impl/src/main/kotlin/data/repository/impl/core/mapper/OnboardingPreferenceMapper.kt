package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.OnboardingPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.OnboardingModel

/**
 * Mapper for converting between [OnboardingModel] and [OnboardingPreference].
 */
internal object OnboardingPreferenceMapper :
    ModelResourceMapper<OnboardingModel, OnboardingPreference> {
    override val toModel: Mapper<OnboardingPreference, OnboardingModel> =
        Mapper { input ->
            OnboardingModel(
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
