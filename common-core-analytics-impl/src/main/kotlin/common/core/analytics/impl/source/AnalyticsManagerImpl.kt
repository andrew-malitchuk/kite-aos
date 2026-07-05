package common.core.analytics.impl.source

import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsManager
import common.core.analytics.api.source.AnalyticsProvider

/**
 * Default implementation of [AnalyticsManager] that fan-outs events to all registered providers.
 *
 * Each [AnalyticsProvider] in [providers] is invoked in sequence for every event. Failures in
 * individual providers are swallowed via [runCatching] so that a misbehaving provider cannot
 * prevent others from receiving the event.
 *
 * @param providers The list of analytics back-ends to dispatch events to.
 * @see AnalyticsManager
 * @see AnalyticsProvider
 */
internal class AnalyticsManagerImpl(
    private val providers: List<AnalyticsProvider>,
) : AnalyticsManager {

    /**
     * Dispatches [event] to every registered provider, swallowing individual provider errors.
     *
     * @param event The analytics event to track.
     */
    override fun track(event: AnalyticsEvent) {
        providers.forEach { provider ->
            // NOTE: runCatching isolates provider failures so that one broken backend
            // does not block delivery to the remaining providers.
            runCatching { provider.track(event) }
        }
    }
}
