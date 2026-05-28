package common.core.analytics.provider.console.source

import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsProvider
import org.koin.core.annotation.Single

@Single(binds = [AnalyticsProvider::class])
internal class ConsoleAnalyticsProvider : AnalyticsProvider {
    override fun track(event: AnalyticsEvent) {
        println("[Analytics] ${event.group ?: "-"} :: ${event.name} -> ${event.params}")
    }
}
