package common.core.analytics.api.source

/**
 * A single analytics back-end that knows how to record an [AnalyticsEvent].
 *
 * Implement this functional interface to integrate a specific analytics destination
 * (e.g., Firebase Analytics, console logging, or a custom back-end). Multiple providers
 * can be registered at once; the [AnalyticsManager] fans events out to all of them.
 *
 * @see AnalyticsManager
 * @see AnalyticsEvent
 */
public fun interface AnalyticsProvider {

    /**
     * Records [event] in the underlying analytics back-end.
     *
     * Implementations must not throw — any errors should be caught and handled internally
     * so that a failing provider does not prevent other providers from receiving the event.
     *
     * @param event The analytics event to record.
     */
    public fun track(event: AnalyticsEvent)
}
