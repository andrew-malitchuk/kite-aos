package common.core.analytics.provider.firebase.source

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsProvider
import org.koin.core.annotation.Single

/**
 * An [AnalyticsProvider] that forwards events to Firebase Analytics.
 *
 * Each [AnalyticsEvent] is converted to a Firebase [Bundle] where:
 * - [AnalyticsEvent.group] is stored under the `"group"` key (if present).
 * - Each entry in [AnalyticsEvent.params] is typed and stored using the appropriate
 *   `Bundle.put*` overload. Unknown types are stored as their `toString()` string.
 *
 * This provider is only active in `gms` build variants where Firebase dependencies
 * are on the compile classpath.
 *
 * @param context The [Context] used to obtain the [FirebaseAnalytics] singleton.
 * @see AnalyticsProvider
 * @see AnalyticsEvent
 */
@Single(binds = [AnalyticsProvider::class])
internal class FirebaseAnalyticsProvider(context: Context) : AnalyticsProvider {

    // NOTE: FirebaseAnalytics is a heavyweight singleton; obtain it once per provider lifetime.
    private val firebase = FirebaseAnalytics.getInstance(context)

    /**
     * Converts [event] into a Firebase-compatible [Bundle] and logs it via [FirebaseAnalytics.logEvent].
     *
     * @param event The analytics event to forward to Firebase.
     */
    override fun track(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            event.group?.let { putString("group", it) }
            event.params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    null -> Unit
                    // NOTE: Unsupported param types are serialised as strings to avoid
                    // a runtime crash, at the cost of losing type information.
                    else -> putString(key, value.toString())
                }
            }
        }
        firebase.logEvent(event.name, bundle)
    }
}
