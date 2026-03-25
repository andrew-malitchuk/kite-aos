package data.preferences.impl.core.configure

/**
 * An object that defines the filenames used for storing preferences.
 *
 * This object contains nested objects for organizing various preference filenames, such as `CONFIGURE` and `USER`.
 */
internal object PreferenceConfigure {
    /**
     * An object that contains constants for filenames used in the preference data store.
     */
    internal object Filename {
        /**
         * The filename for storing the `Theme` preferences.
         */
        internal const val THEME = "theme.pb"

        internal const val ONBOARDING = "onboarding.pb"

        internal const val DASHBOARD = "dashboard.pb"

        internal const val DOCK = "dock.pb"

        internal const val MOVE_DETECTOR = "move_detector.pb"

        internal const val MQTT = "mqtt.pb"

        internal const val LANGUAGE = "language.pb"
    }
}
