package data.database.impl.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import data.database.api.source.resource.ApplicationDatabase
import data.database.impl.core.database.RoomDatabase.Companion.VERSION
import data.database.impl.source.dao.ApplicationDao

/**
 * The Room database for the application.
 */
@Database(
    version = VERSION,
    entities = [
        ApplicationDatabase::class,
    ],
    exportSchema = true,
)
public abstract class RoomDatabase : RoomDatabase() {
    /**
     * Gets the DAO for application resources.
     */
    public abstract fun getApplicationDao(): ApplicationDao

    public companion object {
        /**
         * The current database version.
         */
        public const val VERSION: Int = 1
    }
}
