package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.platform.api.source.resource.ApplicationPlatform
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.ApplicationModel

/**
 * Mapper for converting between [ApplicationModel] and [ApplicationPlatform].
 */
internal object ApplicationPlatformMapper :
    ModelResourceMapper<ApplicationModel, ApplicationPlatform> {
    override val toModel: Mapper<ApplicationPlatform, ApplicationModel> =
        Mapper { input ->
            ApplicationModel(
                id = null,
                name = input.name,
                packageName = input.packageName,
                icon = input.icon,
            )
        }
    override val toResource: Mapper<ApplicationModel, ApplicationPlatform> =
        Mapper { input ->
            ApplicationPlatform(
                name = input.name,
                packageName = input.packageName,
                icon = input.icon,
            )
        }
}
