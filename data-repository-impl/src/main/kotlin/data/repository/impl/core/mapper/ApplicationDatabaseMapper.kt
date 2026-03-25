package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.database.api.source.resource.ApplicationDatabase
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.ApplicationModel

/**
 * Mapper for converting between [ApplicationModel] and [ApplicationDatabase].
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
