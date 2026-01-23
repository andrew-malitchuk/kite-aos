package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DashboardPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.DashboardModel

/**
 * Mapper for converting between [DashboardModel] and [DashboardPreference].
 */
internal object DashboardPreferenceMapper :
    ModelResourceMapper<DashboardModel, DashboardPreference> {
    override val toModel: Mapper<DashboardPreference, DashboardModel> = Mapper { input ->
        DashboardModel(
            dashboardUrl = input.dashboardUrl ?: "",
            whitelistUrl = input.whitelistUrl ?: ""
        )
    }
    override val toResource: Mapper<DashboardModel, DashboardPreference> = Mapper { input ->
        DashboardPreference(
            dashboardUrl = input.dashboardUrl,
            whitelistUrl = input.whitelistUrl
        )
    }
}
