package common.core.analytics.api.source

public data class AnalyticsEvent(
    public val group: String?,
    public val name: String,
    public val params: Map<String, Any?>,
)
