package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DockPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.DockPositionModel

/**
 * Mapper for converting between [DockPositionModel] and [DockPreference].
 *
 * Handles bidirectional mapping between the domain representation of dock position
 * and the preference storage resource. When converting from [DockPreference] to
 * [DockPositionModel], the stored integer position is matched against known
 * [DockPositionModel.Position] entries, falling back to [DockPositionModel.Position.Left]
 * if no match is found.
 *
 * @see DockPositionModel
 * @see DockPreference
 * @see ModelResourceMapper
 * @since 0.0.1
 */
internal object DockPositionPreferenceMapper :
    ModelResourceMapper<DockPositionModel, DockPreference> {

    override val toModel: Mapper<DockPreference, DockPositionModel> =
        Mapper { input ->
            DockPositionModel(
                // Match the stored position value against known entries; default to Left
                position =
                DockPositionModel.Position.entries.firstOrNull { it.position == input.position }
                    ?: DockPositionModel.Position.Left,
            )
        }

    override val toResource: Mapper<DockPositionModel, DockPreference> =
        Mapper { input ->
            DockPreference(
                position = input.position.position,
            )
        }
}
