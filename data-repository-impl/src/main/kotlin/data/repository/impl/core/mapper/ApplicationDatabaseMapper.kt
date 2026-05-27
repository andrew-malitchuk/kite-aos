package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.database.api.source.resource.ApplicationDatabase
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.ApplicationModel

/**
 * Mapper for converting between [ApplicationModel] and [ApplicationDatabase].
 *
 * Handles bidirectional mapping between the domain representation of an application
 * and its Room database entity. Note that when mapping from [ApplicationDatabase] to
 * [ApplicationModel], the [ApplicationModel.chosen] flag is always set to `true` since
 * persisted applications are inherently chosen by the user.
 *
 * @see ApplicationModel
 * @see ApplicationDatabase
 * @see ModelResourceMapper
 * @see ApplicationPlatformMapper
 * @since 0.0.1
 */
internal object ApplicationDatabaseMapper :
    ModelResourceMapper<ApplicationModel, ApplicationDatabase> {

    override val toModel: Mapper<ApplicationDatabase, ApplicationModel> =
        Mapper { input ->
            ApplicationModel(
                id = input.id,
                name = input.name,
                packageName = input.packageName,
                icon = input.icon,
                // Database-persisted applications are always marked as chosen
                chosen = true,
            )
        }

    override val toResource: Mapper<ApplicationModel, ApplicationDatabase> =
        Mapper { input ->
            ApplicationDatabase(
                id = input.id,
                name = input.name,
                packageName = input.packageName,
                icon = input.icon,
            )
        }
}
