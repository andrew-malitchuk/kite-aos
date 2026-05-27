package data.database.impl.source.dao.base

import data.core.source.resource.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Base abstract class for Room Data Access Objects (DAOs).
 *
 * Defines the standard CRUD operations every concrete DAO must implement.
 * Concrete subclasses annotate each method with the appropriate Room annotation
 * (`@Query`, `@Insert`, `@Update`, etc.).
 *
 * @param DATABASE The [Resource] type this DAO manages.
 * @see data.database.impl.source.dao.ApplicationDao
 * @since 0.0.1
 */
public abstract class Dao<DATABASE : Resource> {
    /**
     * Retrieves a resource by its [id].
     *
     * @param id The primary-key ID of the resource to find.
     * @return The resource if found, or `null`.
     */
    public abstract suspend fun get(id: Int): DATABASE?

    /**
     * Retrieves all resources of type [DATABASE].
     *
     * @return A list of all resources, or `null` if the table is empty.
     */
    public abstract suspend fun get(): List<DATABASE>?

    /**
     * Returns a [Flow] that emits the list of all resources whenever the table changes.
     *
     * @return A reactive flow of resource lists; emits `null` when the table is empty.
     */
    public abstract fun subscribe(): Flow<List<DATABASE>?>

    /**
     * Inserts a [value] into the database.
     *
     * @param value The resource to insert.
     */
    public abstract suspend fun insert(value: DATABASE)

    /**
     * Updates an existing [value] in the database, matched by primary key.
     *
     * @param value The resource to update.
     */
    public abstract suspend fun update(value: DATABASE)

    /**
     * Deletes a resource with the specified [id].
     *
     * @param id The primary-key ID of the resource to delete.
     */
    public abstract suspend fun delete(id: Int)

    /**
     * Deletes all resources from the table.
     */
    public abstract suspend fun deleteAll()
}
