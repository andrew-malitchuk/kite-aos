package data.preferences.api.source.resource

import data.core.source.resource.Resource

public data class MqttPreference(
    val enabled: Boolean? = null,
    val ip: String? = null,
    val port: String? = null,
    val clientId: String? = null,
    val username: String? = null,
    val password: String? = null,
    val friendlyName: String? = null,
) : Resource
