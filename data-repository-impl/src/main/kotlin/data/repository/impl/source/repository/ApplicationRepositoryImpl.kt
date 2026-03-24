package data.repository.impl.source.repository

import data.database.api.source.datasource.ApplicationDatabaseSource
import data.platform.api.source.datasource.ApplicationPlatformSource
import data.repository.impl.core.mapper.ApplicationDatabaseMapper
import data.repository.impl.core.mapper.ApplicationPlatformMapper
import domain.core.source.model.ApplicationModel
import domain.repository.api.source.repository.ApplicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Implementation of [ApplicationRepository] that orchestrates database and platform sources.
 *
 * @property databaseSource The database source for saved applications.
 * @property platformSource The platform source for system-installed applications.
 */
@Single(binds = [ApplicationRepository::class])
internal class ApplicationRepositoryImpl(
    private val databaseSource: ApplicationDatabaseSource,
    private val platformSource: ApplicationPlatformSource,
) : ApplicationRepository {
    override suspend fun loadApplications(): List<ApplicationModel> {
        return databaseSource.getAll().map(ApplicationDatabaseMapper.toModel::map)
    }

    override suspend fun addApplication(application: ApplicationModel) {
        databaseSource.save(application.let(ApplicationDatabaseMapper.toResource::map))
    }

    override suspend fun removeApplication(application: ApplicationModel) {
        databaseSource.delete(application.let(ApplicationDatabaseMapper.toResource::map))
    }

    override fun observeApplications(): Flow<List<ApplicationModel>> {
        return databaseSource.observe().map {
            it.map(ApplicationDatabaseMapper.toModel::map)
        }
    }

    override suspend fun getApplications(): List<ApplicationModel> {
        return platformSource.getApplications().map(ApplicationPlatformMapper.toModel::map)
    }

    override suspend fun getApplication(packageName: String): ApplicationModel? {
        return platformSource.getApplication(packageName)?.let(ApplicationPlatformMapper.toModel::map)
    }
}
