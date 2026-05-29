package data.preferences.api.source.resource

import data.core.source.resource.Resource

public data class AutoRebootPreference(
    val enabled: Boolean? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val intervalDays: Int? = null,
) : Resource
