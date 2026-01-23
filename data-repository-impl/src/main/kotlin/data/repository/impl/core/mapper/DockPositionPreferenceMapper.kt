package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DockPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.DockPositionModel

/**
 * Mapper for converting between [DockPositionModel] and [DockPreference].
 */
internal object DockPositionPreferenceMapper :
    ModelResourceMapper<DockPositionModel, DockPreference> {
    override val toModel: Mapper<DockPreference, DockPositionModel> = Mapper { input ->
        DockPositionModel(
            position = DockPositionModel.Position.entries.firstOrNull { it.position == input.position }
                ?: DockPositionModel.Position.Left
        )
    }
    override val toResource: Mapper<DockPositionModel, DockPreference> = Mapper { input ->
        DockPreference(
            position = input.position.position
        )
    }
}
