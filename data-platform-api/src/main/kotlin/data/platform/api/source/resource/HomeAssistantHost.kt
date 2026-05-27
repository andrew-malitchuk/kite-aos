package data.platform.api.source.resource

import data.core.source.resource.Resource

/**
 * Data resource representing a discovered Home Assistant host.
 *
 * @property url The base URL of the host (e.g., "http://192.168.1.10:8123").
 * @property source Discovery method label: "quick" for mDNS, "scan" for subnet scan.
 * @since 0.0.5
 */
public data class HomeAssistantHost(
    public val url: String,
    public val source: String,
) : Resource
