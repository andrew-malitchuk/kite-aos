package data.preferences.api.source.resource

import data.core.source.resource.Resource

public data class DashboardPreference(
    val dashboardUrl: String? = null,
    val whitelistUrl: String? = null
) : Resource