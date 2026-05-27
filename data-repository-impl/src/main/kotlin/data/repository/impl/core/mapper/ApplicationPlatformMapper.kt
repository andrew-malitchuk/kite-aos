package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.platform.api.source.resource.ApplicationPlatform
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.ApplicationModel

/**
 * Mapper for converting between [ApplicationModel] and [ApplicationPlatform].
 *
 * Handles bidirectional mapping between the domain representation of an application
 * and the platform-level (system-installed) application resource. Note that when mapping
 * from [ApplicationPlatform] to [ApplicationModel], the [ApplicationModel.id] is set to `null`
 * because platform-sourced applications have not yet been persisted to the database.
 *
 * @see ApplicationModel
 * @see ApplicationPlatform
 * @see ModelResourceMapper
 * @see ApplicationDatabaseMapper
 * @since 0.0.1
 */
internal object ApplicationPlatformMapper :
    ModelResourceMapper<ApplicationModel, ApplicationPlatform> {

    override val toModel: Mapper<ApplicationPlatform, ApplicationModel> =
        Mapper { input ->
            ApplicationModel(
                // Platform-sourced applications have no database ID yet
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
