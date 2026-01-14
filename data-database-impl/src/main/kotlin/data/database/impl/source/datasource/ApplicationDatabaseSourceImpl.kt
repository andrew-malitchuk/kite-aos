package data.database.impl.source.datasource

import data.database.api.source.datasource.ApplicationDatabaseSource
import data.database.api.source.resource.ApplicationDatabase
import data.database.impl.source.dao.ApplicationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import org.koin.core.annotation.Single

/**
 * Implementation of [ApplicationDatabaseSource] using Room via [ApplicationDao].
 *
 * @property dao The application DAO used for data access.
 */
@Single(binds = [ApplicationDatabaseSource::class])
internal class ApplicationDatabaseSourceImpl(
    private val dao: ApplicationDao
) : ApplicationDatabaseSource() {

    override suspend fun save(value: ApplicationDatabase) {
        dao.insert(value)
    }

    override suspend fun getAll(): List<ApplicationDatabase> {
        return dao.get() ?: listOf()
    }

    override fun observe(): Flow<List<ApplicationDatabase>> {
        return dao.subscribe().filterNotNull()
    }

    override suspend fun delete(value: ApplicationDatabase) {
        value.id?.let { dao.delete(it) }
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

}