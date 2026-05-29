package data.preferences.api.source.resource

import data.core.source.resource.Resource

public data class ScreensaverPreference(
    val enabled: Boolean? = null,
    val activationDelay: Long? = null,
    val slideInterval: Long? = null,
    val showClock: Boolean? = null,
    val source: Int? = null,
    val localFolderUri: String? = null,
) : Resource
