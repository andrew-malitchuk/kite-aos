package common.core.analytics.api.source

/**
 * Represents a single analytics event to be tracked by an [AnalyticsManager].
 *
 * Events are grouped by an optional [group] identifier (e.g., the feature name) and
 * carry a mandatory [name] plus an arbitrary map of [params] for additional context.
 *
 * @property group An optional logical grouping for the event (e.g., `"onboarding"`, `"settings"`).
 *   Corresponds to the [Group] annotation on the tracking interface class.
 * @property name The event name as declared via the [Event] annotation on the tracking method.
 * @property params A key-value map of additional parameters to attach to the event.
 *   Values may be `null` or any primitive-compatible type.
 * @see AnalyticsManager
 * @see AnalyticsProvider
 * @see Event
 * @see Group
 * @see Param
 */
public data class AnalyticsEvent(
    public val group: String?,
    public val name: String,
    public val params: Map<String, Any?>,
)
