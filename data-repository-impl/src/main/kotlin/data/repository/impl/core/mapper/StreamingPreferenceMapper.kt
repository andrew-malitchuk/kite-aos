package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.StreamingPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.StreamingModel

internal object StreamingPreferenceMapper :
    ModelResourceMapper<StreamingModel, StreamingPreference> {

    override val toModel: Mapper<StreamingPreference, StreamingModel> =
        Mapper { input ->
            StreamingModel(
                enabled = input.enabled,
                port = input.port,
                quality = input.quality,
                fps = input.fps,
                rotation = input.rotation,
            )
        }

    override val toResource: Mapper<StreamingModel, StreamingPreference> =
        Mapper { input ->
            StreamingPreference(
                enabled = input.enabled,
                port = input.port,
                quality = input.quality,
                fps = input.fps,
                rotation = input.rotation,
            )
        }
}
