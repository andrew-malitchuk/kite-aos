package data.database.api.source.datasource.base

import data.core.source.resource.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Base abstract class for database data sources.
 *
 * Defines the standard CRUD contract that every database-backed data source must implement.
 * Subclasses bind a concrete [Resource] type to Room DAO operations.
 *
 * @param DATABASE The type of the [Resource] stored in the database.
 * @see data.database.api.source.datasource.ApplicationDatabaseSource
 * @since 0.0.1
 */
public abstract class DatabaseSource<DATABASE : Resource> {
    /**
     * Saves a [value] to the database.
     *
     * If the resource already exists, the behavior (ignore, replace, etc.) is
     * determined by the underlying DAO conflict strategy.
     *
     * @param value The resource to save.
     */
    public abstract suspend fun save(value: DATABASE)

    /**
     * Retrieves all resources of type [DATABASE] from the database.
     *
     * @return A list of all resources. Returns an empty list when the table contains no rows.
     */
    public abstract suspend fun getAll(): List<DATABASE>

    /**
     * Returns a [Flow] that emits the list of all resources whenever the database changes.
     *
     * The flow re-emits on every table modification, making it suitable for
     * reactive UI bindings.
     *
     * @return A flow of resource lists.
     */
    public abstract fun observe(): Flow<List<DATABASE>>

    /**
     * Deletes a specific [value] from the database.
     *
     * @param value The resource to delete.
     */
    public abstract suspend fun delete(value: DATABASE)

    /**
     * Deletes all resources from the table managed by this source.
     */
    public abstract suspend fun deleteAll()
}
