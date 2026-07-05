package common.core.analytics.api.source

/**
 * Marks an interface or class as belonging to a named analytics group.
 *
 * When [AnalyticsProxy] processes a method call, it reads this annotation from the
 * declaring class to populate [AnalyticsEvent.group]. Typically applied to a tracking
 * interface that collects all events for one feature (e.g., `@Group("onboarding")`).
 *
 * @property name The group identifier to attach to every event fired from the annotated class.
 * @see Event
 * @see Param
 * @see AnalyticsEvent
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Group(public val name: String)

/**
 * Marks a function as an analytics event with the given [name].
 *
 * [AnalyticsProxy] inspects each invoked method for this annotation to determine the
 * event name. Methods without this annotation are silently ignored — no event is fired.
 *
 * @property name The event name to use in [AnalyticsEvent.name].
 * @see Group
 * @see Param
 * @see AnalyticsEvent
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Event(public val name: String)

/**
 * Tags a function parameter with a named analytics parameter key.
 *
 * [AnalyticsProxy] pairs each annotated parameter value with its [name] when
 * building the [AnalyticsEvent.params] map. Parameters without this annotation
 * are excluded from the event payload.
 *
 * @property name The parameter key to use in [AnalyticsEvent.params].
 * @see Group
 * @see Event
 * @see AnalyticsEvent
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Param(public val name: String)
