package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.CameraPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.CameraSourceModel

/**
 * Mapper for converting between [CameraSourceModel] and [CameraPreference].
 *
 * When converting from [CameraPreference] to [CameraSourceModel] the stored mode string is
 * matched against known [CameraSourceModel] entries, falling back to [CameraSourceModel.Auto]
 * if no match is found (covers the empty-string default of a fresh Proto DataStore).
 *
 * @see CameraSourceModel
 * @see CameraPreference
 * @since 1.4.0
 */
internal object CameraPreferenceMapper :
    ModelResourceMapper<CameraSourceModel, CameraPreference> {

    override val toModel: Mapper<CameraPreference, CameraSourceModel> =
        Mapper { input ->
            CameraSourceModel.entries.firstOrNull { it.mode == input.mode } ?: CameraSourceModel.Auto
        }

    override val toResource: Mapper<CameraSourceModel, CameraPreference> =
        Mapper { input ->
            CameraPreference(
                mode = input.mode,
            )
        }
}
