package common.core.analytics.api.source

public fun interface AnalyticsProvider {
    public fun track(event: AnalyticsEvent)
}
