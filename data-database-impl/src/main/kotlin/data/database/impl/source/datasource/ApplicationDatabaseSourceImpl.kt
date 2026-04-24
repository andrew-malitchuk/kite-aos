package data.database.impl.source.datasource

import data.database.api.source.datasource.ApplicationDatabaseSource
import data.database.api.source.resource.ApplicationDatabase
import data.database.impl.source.dao.ApplicationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import org.koin.core.annotation.Single

/**
 * Implementation of [ApplicationDatabaseSource] backed by Room via [ApplicationDao].
 *
 * Bound to the [ApplicationDatabaseSource] interface through Koin's `@Single(binds = ...)`
 * annotation, so consumers depend only on the API-layer abstraction.
 *
 * @property dao The Room DAO used for all database operations on the `application` table.
 * @see ApplicationDatabaseSource
 * @see ApplicationDao
 * @since 0.0.1
 */
@Single(binds = [ApplicationDatabaseSource::class])
internal class ApplicationDatabaseSourceImpl(
    private val dao: ApplicationDao,
) : ApplicationDatabaseSource() {

    override suspend fun save(value: ApplicationDatabase) {
        dao.insert(value)
    }

    override suspend fun getAll(): List<ApplicationDatabase> {
        // DAO returns null for an empty table; coerce to an empty list for a safer API contract.
        return dao.get() ?: listOf()
    }

    override fun observe(): Flow<List<ApplicationDatabase>> {
        // Room's query Flow may emit null when the table is empty; filter those out
        // so downstream collectors always receive a non-null list.
        return dao.subscribe().filterNotNull()
    }

    override suspend fun delete(value: ApplicationDatabase) {
        // Only attempt deletion when the entity has been persisted (non-null id).
        // Unsaved entities (id == null) are silently ignored.
        value.id?.let { dao.delete(it) }
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
