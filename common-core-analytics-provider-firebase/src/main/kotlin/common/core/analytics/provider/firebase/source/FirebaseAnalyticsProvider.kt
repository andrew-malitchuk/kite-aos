package common.core.analytics.provider.firebase.source

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import common.core.analytics.api.source.AnalyticsEvent
import common.core.analytics.api.source.AnalyticsProvider
import org.koin.core.annotation.Single

@Single(binds = [AnalyticsProvider::class])
internal class FirebaseAnalyticsProvider(context: Context) : AnalyticsProvider {

    private val firebase = FirebaseAnalytics.getInstance(context)

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
                    else -> putString(key, value.toString())
                }
            }
        }
        firebase.logEvent(event.name, bundle)
    }
}
