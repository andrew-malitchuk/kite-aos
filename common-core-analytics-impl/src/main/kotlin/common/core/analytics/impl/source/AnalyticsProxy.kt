package common.core.analytics.impl.source

import common.core.analytics.api.source.AnalyticsManager
import java.lang.reflect.Proxy

/**
 * Factory that creates annotation-driven analytics tracking proxies.
 *
 * Use [create] to obtain a JVM dynamic proxy for a tracking interface whose methods are
 * annotated with [common.core.analytics.api.source.Event],
 * [common.core.analytics.api.source.Group], and
 * [common.core.analytics.api.source.Param]. When any annotated method is invoked on the
 * returned proxy, [AnalyticsProxyInvocationHandler] translates it into an
 * [common.core.analytics.api.source.AnalyticsEvent] and forwards it to [AnalyticsManager].
 *
 * Example usage:
 * ```kotlin
 * @Group("settings")
 * interface SettingsAnalytics {
 *     @Event("theme_changed")
 *     fun onThemeChanged(@Param("theme") theme: String)
 * }
 *
 * val analytics = AnalyticsProxy.create(SettingsAnalytics::class.java, manager)
 * analytics.onThemeChanged("dark")
 * ```
 *
 * @see AnalyticsProxyInvocationHandler
 * @see AnalyticsManager
 */
public object AnalyticsProxy {

    /**
     * Creates a JVM dynamic proxy for [clazz] that converts annotated method calls into
     * analytics events dispatched via [manager].
     *
     * @param T The tracking interface type.
     * @param clazz The [Class] of the tracking interface.
     * @param manager The [AnalyticsManager] to deliver events to.
     * @return A proxy instance implementing [T].
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> create(clazz: Class<T>, manager: AnalyticsManager): T =
        Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            AnalyticsProxyInvocationHandler(manager),
        ) as T
}
