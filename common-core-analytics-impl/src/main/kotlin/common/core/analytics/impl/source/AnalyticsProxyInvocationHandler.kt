package common.core.analytics.impl.source

import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsManager
import common.core.analytics.api.source.Event
import common.core.analytics.api.source.Group
import common.core.analytics.api.source.Param
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

internal class AnalyticsProxyInvocationHandler(
    private val manager: AnalyticsManager,
) : InvocationHandler {

    override fun invoke(
        proxy: Any?,
        method: Method?,
        args: Array<out Any?>?,
    ): Any? {
        val m = method ?: return null

        if (m.declaringClass == Any::class.java) {
            return when (m.name) {
                "equals" -> proxy === args?.firstOrNull()
                "hashCode" -> System.identityHashCode(proxy)
                "toString" -> "AnalyticsProxy@${Integer.toHexString(System.identityHashCode(proxy))}"
                else -> null
            }
        }

        val eventName = m.getAnnotation(Event::class.java)?.name ?: return Unit
        val group = m.declaringClass.getAnnotation(Group::class.java)?.name
        val params = buildParams(m, args)

        manager.track(AnalyticsEvent(group = group, name = eventName, params = params))
        return Unit
    }

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
