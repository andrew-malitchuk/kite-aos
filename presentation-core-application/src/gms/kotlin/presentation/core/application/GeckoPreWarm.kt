package presentation.core.application

import android.app.Application

// GeckoView is not bundled in the gms flavor — pre-warm is a no-op.
internal fun Application.warmGeckoIfNeeded(): Unit = Unit
