package common.core.analytics.impl.source

import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsManager
import common.core.analytics.api.source.Event
import common.core.analytics.api.source.Group
import common.core.analytics.api.source.Param
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * [InvocationHandler] that converts annotated interface method calls into [AnalyticsEvent]s.
 *
 * Used internally by [AnalyticsProxy]. For each proxy method invocation:
 * - Methods declared on [Any] (`equals`, `hashCode`, `toString`) are handled directly.
 * - Methods without an [Event] annotation are silently ignored.
 * - Methods with an [Event] annotation are translated into an [AnalyticsEvent], collecting
 *   the optional [Group] from the declaring class and [Param]-annotated arguments.
 *
 * @param manager The [AnalyticsManager] that receives the constructed events.
 * @see AnalyticsProxy
 * @see AnalyticsManager
 * @see Event
 * @see Group
 * @see Param
 */
internal class AnalyticsProxyInvocationHandler(
    private val manager: AnalyticsManager,
) : InvocationHandler {

    /**
     * Handles a proxied method invocation.
     *
     * Returns the identity-based result for [Any] methods, `Unit` when the method lacks
     * an [Event] annotation, or `Unit` after firing the event to [manager].
     *
     * @param proxy The proxy instance the method was invoked on.
     * @param method The [Method] being invoked, or `null` if unavailable.
     * @param args The method arguments, or `null` if the method takes no parameters.
     * @return An appropriate return value depending on the method type.
     */
    override fun invoke(
        proxy: Any?,
        method: Method?,
        args: Array<out Any?>?,
    ): Any? {
        val m = method ?: return null

        // Delegate Object methods to identity-based implementations to avoid infinite recursion.
        if (m.declaringClass == Any::class.java) {
            return when (m.name) {
                "equals" -> proxy === args?.firstOrNull()
                "hashCode" -> System.identityHashCode(proxy)
                "toString" -> "AnalyticsProxy@${Integer.toHexString(System.identityHashCode(proxy))}"
                else -> null
            }
        }

        // Methods without @Event are intentionally ignored — no event is fired.
        val eventName = m.getAnnotation(Event::class.java)?.name ?: return Unit
        val group = m.declaringClass.getAnnotation(Group::class.java)?.name
        val params = buildParams(m, args)

        manager.track(AnalyticsEvent(group = group, name = eventName, params = params))
        return Unit
    }

    /**
     * Builds the event parameter map from [Param]-annotated method arguments.
     *
     * Parameters without a [Param] annotation are excluded from the resulting map.
     *
     * @param method The method whose parameter annotations are inspected.
     * @param args The argument values corresponding to [method]'s parameters.
     * @return A map of parameter name to value for all [Param]-annotated arguments.
     */
    private fun buildParams(
        method: Method,
        args: Array<out Any?>?,
    ): Map<String, Any?> {
        if (args == null) return emptyMap()
        return method.parameterAnnotations
            .mapIndexedNotNull { idx, annotations ->
                val paramName = annotations.filterIsInstance<Param>().firstOrNull()?.name
                if (paramName != null) paramName to args.getOrNull(idx) else null
            }
            .toMap()
    }
}
