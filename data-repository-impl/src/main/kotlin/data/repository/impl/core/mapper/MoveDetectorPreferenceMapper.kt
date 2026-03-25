package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.MoveDetectorPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.MoveDetectorModel

/**
 * Mapper for converting between [MoveDetectorModel] and [MoveDetectorPreference].
 */
internal object MoveDetectorPreferenceMapper :
    ModelResourceMapper<MoveDetectorModel, MoveDetectorPreference> {
    override val toModel: Mapper<MoveDetectorPreference, MoveDetectorModel> =
        Mapper { input ->
            MoveDetectorModel(
                enabled = input.enabled,
                sensitivity = input.sensitivity,
                dimDelay = input.dimDelay,
                screenTimeout = input.screenTimeout,
                fabDelay = input.fabDelay,
            )
        }
    override val toResource: Mapper<MoveDetectorModel, MoveDetectorPreference> =
        Mapper { input ->
            MoveDetectorPreference(
                enabled = input.enabled,
                sensitivity = input.sensitivity,
                dimDelay = input.dimDelay,
                screenTimeout = input.screenTimeout,
                fabDelay = input.fabDelay,
            )
        }
}
