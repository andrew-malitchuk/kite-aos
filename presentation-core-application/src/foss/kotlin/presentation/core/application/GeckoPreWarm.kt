package presentation.core.application

import android.app.Application
import presentation.feature.main.source.webview.engine.preWarmGeckoRuntime

internal fun Application.warmGeckoIfNeeded() {
    preWarmGeckoRuntime(applicationContext)
}
