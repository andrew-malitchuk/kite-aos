package data.database.api.core.configure

/**
 * Configuration constants for the database, including the database name and table names.
 */
public object DatabaseConfigure {
    /**
     * The name of the database file.
     */
    public const val DATABASE: String = "yahk"

    /**
     * Table name constants.
     */
    public object Table {
        /**
         * The name of the application table.
         */
        public const val APPLICATION: String = "application"
    }
}
