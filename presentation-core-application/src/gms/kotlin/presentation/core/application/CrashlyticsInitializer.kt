package presentation.core.application

import com.google.firebase.crashlytics.FirebaseCrashlytics

internal object CrashlyticsInitializer {

    fun init() {
        // Guarded: Crashlytics component may be absent in debug builds or when
        // the Firebase console has collection disabled.
        runCatching {
            FirebaseCrashlytics.getInstance().sendUnsentReports()
        }
    }
}
