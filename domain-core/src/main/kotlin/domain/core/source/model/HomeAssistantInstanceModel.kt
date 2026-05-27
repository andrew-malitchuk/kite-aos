package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Represents a discovered Home Assistant instance on the local network.
 *
 * @property url The base URL of the instance (e.g., "http://192.168.1.10:8123").
 * @property source How the instance was found during discovery.
 * @since 0.0.5
 */
public data class HomeAssistantInstanceModel(
    public val url: String,
    public val source: DiscoverySource,
) : Model {

    /** Indicates how the Home Assistant instance was discovered. */
    public enum class DiscoverySource {
        /** Found via a well-known mDNS hostname probe (e.g., homeassistant.local). */
        Quick,

        /** Found via a parallel local subnet IP scan. */
        Scan,
    }
}
