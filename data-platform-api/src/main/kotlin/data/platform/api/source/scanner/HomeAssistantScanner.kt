package data.platform.api.source.scanner

import data.platform.api.source.resource.HomeAssistantHost

/**
 * Contract for discovering Home Assistant instances on the local network.
 *
 * Implementations perform mDNS hostname probes first, then fall back to a parallel
 * subnet scan, capped at a total timeout.
 *
 * @since 0.0.5
 */
public interface HomeAssistantScanner {

    /**
     * Scans the local network for reachable Home Assistant instances.
     *
     * @return A list of discovered [HomeAssistantHost] instances. Empty if none found or
     *   the total timeout is exceeded.
     */
    public suspend fun discover(): List<HomeAssistantHost>
}
