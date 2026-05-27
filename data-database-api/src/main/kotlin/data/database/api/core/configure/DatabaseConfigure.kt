package data.database.api.core.configure

/**
 * Configuration constants for the database, including the database name and table names.
 *
 * Centralizes all database-related naming to ensure consistency between
 * [Entity][androidx.room.Entity] annotations and raw SQL queries.
 *
 * @see data.database.api.source.resource.ApplicationDatabase
 * @see data.database.impl.core.database.RoomDatabase
 * @since 0.0.1
 */
public object DatabaseConfigure {
    /**
     * The name of the database file used when constructing the Room database instance.
     */
    public const val DATABASE: String = "yahk"

    /**
     * Table name constants used in [Entity][androidx.room.Entity] annotations and raw SQL queries.
     *
     * @since 0.0.1
     */
    public object Table {
        /**
         * The name of the application table storing installed application metadata.
         *
         * @see data.database.api.source.resource.ApplicationDatabase
         */
        public const val APPLICATION: String = "application"
    }
}
