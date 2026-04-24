package data.preferences.impl.core.configure

/**
 * Configuration object that defines the filenames used for Proto DataStore persistence.
 *
 * Each constant corresponds to a `.pb` file that stores a specific category of preference
 * data using Protocol Buffers serialization format.
 *
 * @see data.preferences.impl.di.DataPreferencesImplModule
 * @since 0.0.1
 */
internal object PreferenceConfigure {

    /**
     * Contains all Proto DataStore filename constants.
     *
     * Each filename follows the `{preference_name}.pb` convention and is used by
     * [data.preferences.impl.di.DataPreferencesImplModule] to create the corresponding
     * `DataStoreFactory` instances.
     *
     * @since 0.0.1
     */
    internal object Filename {

        /**
         * The filename for storing the theme preferences (e.g., dark/light mode).
         */
        internal const val THEME = "theme.pb"

        /**
         * The filename for storing the onboarding completion state.
         */
        internal const val ONBOARDING = "onboarding.pb"

        /**
         * The filename for storing the dashboard URL configuration.
         */
        internal const val DASHBOARD = "dashboard.pb"

        /**
         * The filename for storing the dock position preferences.
         */
        internal const val DOCK = "dock.pb"

        /**
         * The filename for storing the motion detector configuration.
         */
        internal const val MOVE_DETECTOR = "move_detector.pb"

        /**
         * The filename for storing the MQTT broker connection configuration.
         */
        internal const val MQTT = "mqtt.pb"

        /**
         * The filename for storing the language/locale preferences.
         */
        internal const val LANGUAGE = "language.pb"
    }
}
