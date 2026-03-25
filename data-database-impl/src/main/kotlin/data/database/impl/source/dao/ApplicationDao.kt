package data.database.impl.source.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import data.database.api.core.configure.DatabaseConfigure
import data.database.api.source.resource.ApplicationDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO implementation for [ApplicationDatabase] entities.
 */
@Dao
public abstract class ApplicationDao :
    data.database.impl.source.dao.base.Dao<ApplicationDatabase>() {
    @Query("SELECT * FROM ${DatabaseConfigure.Table.APPLICATION}")
    public abstract override suspend fun get(): List<ApplicationDatabase>?

    @Query("SELECT * FROM ${DatabaseConfigure.Table.APPLICATION} WHERE id=:id")
    public abstract override suspend fun get(id: Int): ApplicationDatabase?

    @Query("SELECT * FROM ${DatabaseConfigure.Table.APPLICATION}")
    public abstract override fun subscribe(): Flow<List<ApplicationDatabase>?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract override suspend fun insert(value: ApplicationDatabase)

    @Update
    public abstract override suspend fun update(value: ApplicationDatabase)

    @Query("DELETE FROM ${DatabaseConfigure.Table.APPLICATION} WHERE id=:id")
    public abstract override suspend fun delete(id: Int)

    @Query("DELETE FROM ${DatabaseConfigure.Table.APPLICATION}")
    public abstract override suspend fun deleteAll()
}
