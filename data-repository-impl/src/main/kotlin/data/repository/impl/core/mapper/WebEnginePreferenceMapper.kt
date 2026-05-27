package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.WebEnginePreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.WebEngineModel

/**
 * Mapper for converting between [WebEngineModel] and [WebEnginePreference].
 *
 * When converting from [WebEnginePreference] to [WebEngineModel], the stored id is matched
 * against known [WebEngineModel] entries, falling back to [WebEngineModel.AndroidWebView]
 * if no match is found.
 *
 * @see WebEngineModel
 * @see WebEnginePreference
 * @since 0.0.4
 */
internal object WebEnginePreferenceMapper :
    ModelResourceMapper<WebEngineModel, WebEnginePreference> {

    override val toModel: Mapper<WebEnginePreference, WebEngineModel> =
        Mapper { input ->
            WebEngineModel.entries.firstOrNull {
                it.id == input.engine
            } ?: WebEngineModel.AndroidWebView
        }

    override val toResource: Mapper<WebEngineModel, WebEnginePreference> =
        Mapper { input ->
            WebEnginePreference(
                engine = input.id,
            )
        }
}
