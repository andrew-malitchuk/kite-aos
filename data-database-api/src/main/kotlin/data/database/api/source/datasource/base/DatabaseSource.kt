package data.database.api.source.datasource.base

import data.core.source.resource.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Base abstract class for database data sources.
 *
 * @param DATABASE The type of the [Resource] stored in the database.
 */
public abstract class DatabaseSource<DATABASE : Resource> {
    /**
     * Saves a [value] to the database.
     *
     * @param value The resource to save.
     */
    public abstract suspend fun save(value: DATABASE)

    /**
     * Retrieves all resources of type [DATABASE] from the database.
     *
     * @return A list of all resources.
     */
    public abstract suspend fun getAll(): List<DATABASE>

    /**
     * Returns a [Flow] that emits the list of all resources whenever the database changes.
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