package common.core.analytics.impl.source

import common.core.analytics.api.source.AnalyticsManager
import java.lang.reflect.Proxy

public object AnalyticsProxy {
    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> create(clazz: Class<T>, manager: AnalyticsManager): T =
        Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            AnalyticsProxyInvocationHandler(manager),
        ) as T
}
