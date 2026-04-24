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
 * Room DAO for [ApplicationDatabase] entities stored in the
 * **`application`** table ([DatabaseConfigure.Table.APPLICATION]).
 *
 * All queries target the table defined in [DatabaseConfigure.Table.APPLICATION].
 * Inserts use [OnConflictStrategy.IGNORE], so duplicate primary keys are silently skipped.
 *
 * @see ApplicationDatabase
 * @see data.database.impl.source.dao.base.Dao
 * @see data.database.impl.source.datasource.ApplicationDatabaseSourceImpl
 * @since 0.0.1
 */
@Dao
public abstract class ApplicationDao :
    data.database.impl.source.dao.base.Dao<ApplicationDatabase>() {

    /**
     * Retrieves all rows from the `application` table.
     *
     * @return A list of all [ApplicationDatabase] entities, or `null` if the table is empty.
     */
    @Query("SELECT * FROM ${DatabaseConfigure.Table.APPLICATION}")
    public abstract override suspend fun get(): List<ApplicationDatabase>?

    /**
     * Retrieves a single [ApplicationDatabase] row by its primary key.
     *
     * @param id The auto-generated primary key of the row.
     * @return The matching entity, or `null` if no row exists with the given [id].
     */
    @Query("SELECT * FROM ${DatabaseConfigure.Table.APPLICATION} WHERE id=:id")
    public abstract override suspend fun get(id: Int): ApplicationDatabase?

    /**
     * Observes all rows from the `application` table as a reactive [Flow].
     *
     * Room invalidates the query and re-emits whenever the table is modified.
     *
     * @return A [Flow] emitting the current list of entities, or `null` when empty.
     */
    @Query("SELECT * FROM ${DatabaseConfigure.Table.APPLICATION}")
    public abstract override fun subscribe(): Flow<List<ApplicationDatabase>?>

    /**
     * Inserts a new [ApplicationDatabase] row.
     *
     * Uses [OnConflictStrategy.IGNORE] -- if a row with the same primary key already
     * exists, the insert is silently skipped without throwing an exception.
     *
     * @param value The entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract override suspend fun insert(value: ApplicationDatabase)

    /**
     * Updates an existing [ApplicationDatabase] row matched by primary key.
     *
     * @param value The entity containing updated field values.
     */
    @Update
    public abstract override suspend fun update(value: ApplicationDatabase)

    /**
     * Deletes a single row from the `application` table by primary key.
     *
     * @param id The primary key of the row to delete.
     */
    @Query("DELETE FROM ${DatabaseConfigure.Table.APPLICATION} WHERE id=:id")
    public abstract override suspend fun delete(id: Int)

    /**
     * Deletes all rows from the `application` table.
     */
    @Query("DELETE FROM ${DatabaseConfigure.Table.APPLICATION}")
    public abstract override suspend fun deleteAll()
}
