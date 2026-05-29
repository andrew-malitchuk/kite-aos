package data.preferences.api.source.resource

import data.core.source.resource.Resource

public data class StreamingPreference(
    val enabled: Boolean? = null,
    val port: Int? = null,
    val quality: Int? = null,
    val fps: Int? = null,
    val rotation: Int? = null,
) : Resource
