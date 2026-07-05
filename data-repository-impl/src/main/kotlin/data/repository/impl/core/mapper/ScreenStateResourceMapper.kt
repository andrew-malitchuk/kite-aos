package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.repository.impl.core.mapper.base.ModelResourceMapper
import data.runtime.api.source.resource.ScreenStateResource
import domain.core.source.model.ScreenStateModel

/**
 * Mapper for converting between [ScreenStateModel] and [ScreenStateResource].
 *
 * Handles bidirectional mapping between the domain-layer sealed class representing the kiosk
 * screen state and the runtime data-layer resource. Each subtype maps directly to its
 * counterpart with no transformation logic.
 *
 * @see ScreenStateModel
 * @see ScreenStateResource
 * @see ModelResourceMapper
 * @since 0.0.1
 */
internal object ScreenStateResourceMapper : ModelResourceMapper<ScreenStateModel, ScreenStateResource> {

    override val toModel: Mapper<ScreenStateResource, ScreenStateModel> =
        Mapper { input ->
            when (input) {
                ScreenStateResource.Active -> ScreenStateModel.Active
                ScreenStateResource.Screensaver -> ScreenStateModel.Screensaver
            }
        }

    override val toResource: Mapper<ScreenStateModel, ScreenStateResource> =
        Mapper { input ->
            when (input) {
                ScreenStateModel.Active -> ScreenStateResource.Active
                ScreenStateModel.Screensaver -> ScreenStateResource.Screensaver
            }
        }
}
