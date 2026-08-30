package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.InteractionPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.InteractionModel

/**
 * Mapper for converting between [InteractionModel] and [InteractionPreference].
 *
 * @see InteractionModel
 * @see InteractionPreference
 * @see ModelResourceMapper
 * @since 2.2.0
 */
internal object InteractionPreferenceMapper : ModelResourceMapper<InteractionModel, InteractionPreference> {

    override val toResource: Mapper<InteractionModel, InteractionPreference> =
        Mapper { model ->
            InteractionPreference(
                inactivityResetMinutes = model.inactivityResetMinutes,
                volumeGestureEnabled = model.volumeGestureEnabled,
                volumeGesturePressCount = model.volumeGesturePressCount,
            )
        }

    override val toModel: Mapper<InteractionPreference, InteractionModel> =
        Mapper { preference ->
            InteractionModel(
                inactivityResetMinutes = preference.inactivityResetMinutes,
                volumeGestureEnabled = preference.volumeGestureEnabled,
                volumeGesturePressCount = preference.volumeGesturePressCount,
            )
        }
}
