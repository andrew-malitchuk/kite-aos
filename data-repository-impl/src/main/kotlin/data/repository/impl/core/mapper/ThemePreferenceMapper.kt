package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ThemePreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.ThemeModel

/**
 * Mapper for converting between [ThemeModel] and [ThemePreference].
 */
internal object ThemePreferenceMapper :
    ModelResourceMapper<ThemeModel, ThemePreference> {
    override val toModel: Mapper<ThemePreference, ThemeModel> = Mapper { input ->
        ThemeModel.entries.firstOrNull {
            it.mode == input.mode
        } ?: ThemeModel.Light
    }
    override val toResource: Mapper<ThemeModel, ThemePreference> = Mapper { input ->
        ThemePreference(
            mode = input.mode
        )
    }
}