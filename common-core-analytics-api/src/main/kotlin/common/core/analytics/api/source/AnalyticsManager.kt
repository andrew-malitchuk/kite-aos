package common.core.analytics.api.source

/**
 * Central coordinator for dispatching analytics events to all registered providers.
 *
 * Implementations receive an [AnalyticsEvent] and forward it to every registered
 * [AnalyticsProvider]. Callers should obtain a concrete implementation via the
 * [AnalyticsProxy] factory to take advantage of annotation-driven event creation.
 *
 * @see AnalyticsEvent
 * @see AnalyticsProvider
 */
public interface AnalyticsManager {

    /**
     * Dispatches [event] to all registered analytics providers.
     *
     * @param event The analytics event to track.
     */
    public fun track(event: AnalyticsEvent)
}
