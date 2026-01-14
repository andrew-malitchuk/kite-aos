package data.database.impl.di

import android.content.Context
import androidx.room.Room
import data.database.api.core.configure.DatabaseConfigure
import data.database.impl.core.database.RoomDatabase
import data.database.impl.source.dao.ApplicationDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Koin module providing database instances and DAOs for the implementation.
 */
@Module
@ComponentScan("data.database.impl")
public class DataDatabaseImplModule {

    /**
     * Provides the singleton [RoomDatabase] instance.
     *
     * @param appContext The application context required to build the database.
     * @return The application's Room database.
     */
    @Single
    public fun providesAppDatabase(appContext: Context): RoomDatabase {
        return Room
            .databaseBuilder(
                appContext,
                RoomDatabase::class.java,
                DatabaseConfigure.DATABASE,
            )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    /**
     * Provides the singleton [ApplicationDao] instance.
     *
     * @param database The Room database instance.
     * @return The application DAO.
     */
    @Single
    public fun provideApplicationDao(database: RoomDatabase): ApplicationDao {
        return database.getApplicationDao()
    }


}