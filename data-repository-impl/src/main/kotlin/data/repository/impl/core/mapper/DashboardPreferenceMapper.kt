package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DashboardPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.DashboardModel

/**
 * Mapper for converting between [DashboardModel] and [DashboardPreference].
 *
 * Handles bidirectional mapping between the domain representation of dashboard configuration
 * and the preference storage resource. Null URL values from preferences are defaulted to
 * empty strings in the domain model.
 *
 * @see DashboardModel
 * @see DashboardPreference
 * @see ModelResourceMapper
 * @since 0.0.1
 */
internal object DashboardPreferenceMapper :
    ModelResourceMapper<DashboardModel, DashboardPreference> {

    override val toModel: Mapper<DashboardPreference, DashboardModel> =
        Mapper { input ->
            DashboardModel(
                // Default to empty string when preference URL values are null
                dashboardUrl = input.dashboardUrl ?: "",
                whitelistUrl = input.whitelistUrl ?: "",
            )
        }

    override val toResource: Mapper<DashboardModel, DashboardPreference> =
        Mapper { input ->
            DashboardPreference(
                dashboardUrl = input.dashboardUrl,
                whitelistUrl = input.whitelistUrl,
            )
        }
}
