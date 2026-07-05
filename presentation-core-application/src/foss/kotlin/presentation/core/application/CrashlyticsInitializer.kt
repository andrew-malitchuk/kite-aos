package presentation.core.application

/**
 * No-op Crashlytics initializer used in the `foss` build flavor.
 *
 * Firebase Crashlytics is not included in the open-source/FOSS distribution, so
 * [init] intentionally does nothing. This object exists to provide the same call
 * site as the `gms` counterpart without requiring conditional compilation at each
 * usage point.
 *
 * @see presentation.core.application.YahkApplication
 */
internal object CrashlyticsInitializer {

    /** No-op: Firebase Crashlytics is absent in the `foss` flavor. */
    fun init() {
        // No-op: Firebase is not included in the foss flavor.
    }
}
