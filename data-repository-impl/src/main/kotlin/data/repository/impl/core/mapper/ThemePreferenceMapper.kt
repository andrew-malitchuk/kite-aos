package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ThemePreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.ThemeModel

/**
 * Mapper for converting between [ThemeModel] and [ThemePreference].
 *
 * Handles bidirectional mapping between the domain representation of the application
 * theme and the preference storage resource. When converting from [ThemePreference]
 * to [ThemeModel], the stored mode integer is matched against known [ThemeModel] entries,
 * falling back to [ThemeModel.Light] if no match is found.
 *
 * @see ThemeModel
 * @see ThemePreference
 * @see ModelResourceMapper
 * @since 0.0.1
 */
internal object ThemePreferenceMapper :
    ModelResourceMapper<ThemeModel, ThemePreference> {

    override val toModel: Mapper<ThemePreference, ThemeModel> =
        Mapper { input ->
            // Match the stored mode value against known theme entries; default to Light
            ThemeModel.entries.firstOrNull {
                it.mode == input.mode
            } ?: ThemeModel.Light
        }

    override val toResource: Mapper<ThemeModel, ThemePreference> =
        Mapper { input ->
            ThemePreference(
                mode = input.mode,
            )
        }
}
