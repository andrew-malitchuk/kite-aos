package common.core.analytics.provider.console.source

import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsProvider
import org.koin.core.annotation.Single

/**
 * An [AnalyticsProvider] that prints events to standard output.
 *
 * Intended for development and `foss` builds where Firebase Analytics is unavailable.
 * Each event is formatted as:
 * `[Analytics] <group> :: <name> -> <params>`
 *
 * @see AnalyticsProvider
 * @see AnalyticsEvent
 */
@Single(binds = [AnalyticsProvider::class])
internal class ConsoleAnalyticsProvider : AnalyticsProvider {

    /**
     * Prints [event] details to stdout in a human-readable format.
     *
     * @param event The analytics event to print.
     */
    override fun track(event: AnalyticsEvent) {
        println("[Analytics] ${event.group ?: "-"} :: ${event.name} -> ${event.params}")
    }
}
