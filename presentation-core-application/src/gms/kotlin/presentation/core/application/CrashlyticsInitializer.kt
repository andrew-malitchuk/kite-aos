package presentation.core.application

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Firebase Crashlytics initializer used in the `gms` build flavor.
 *
 * Flushes any crash reports that were captured during a previous session but could not
 * be uploaded (e.g., because the device was offline). Called once from
 * [YahkApplication.onCreate] on the main process.
 *
 * @see YahkApplication
 */
internal object CrashlyticsInitializer {

    /**
     * Sends any unsent Crashlytics reports collected from previous sessions.
     *
     * Guarded by [runCatching] because the Crashlytics component may be absent
     * in debug builds or when data collection is disabled in the Firebase console.
     */
    fun init() {
        // Guarded: Crashlytics component may be absent in debug builds or when
        // the Firebase console has collection disabled.
        runCatching {
            FirebaseCrashlytics.getInstance().sendUnsentReports()
        }
    }
}
