package common.core.analytics.impl.source

import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsManager
import common.core.analytics.api.source.AnalyticsProvider

internal class AnalyticsManagerImpl(
    private val providers: List<AnalyticsProvider>,
) : AnalyticsManager {
    override fun track(event: AnalyticsEvent) {
        providers.forEach { provider ->
            runCatching { provider.track(event) }
        }
    }
}
