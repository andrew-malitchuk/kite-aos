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
 * This repository coordinates between two data sources:
 * - [ApplicationDatabaseSource] for managing user-selected (persisted) applications.
 * - [ApplicationPlatformSource] for querying system-installed applications on the device.
 *
 * Mapping between data and domain layers is performed via [ApplicationDatabaseMapper]
 * and [ApplicationPlatformMapper].
 *
 * @property databaseSource The database source for persisted application records.
 * @property platformSource The platform source for system-installed applications.
 * @see ApplicationRepository
 * @see ApplicationDatabaseMapper
 * @see ApplicationPlatformMapper
 * @since 0.0.1
 */
@Single(binds = [ApplicationRepository::class])
internal class ApplicationRepositoryImpl(
    private val databaseSource: ApplicationDatabaseSource,
    private val platformSource: ApplicationPlatformSource,
) : ApplicationRepository {

    /**
     * Loads all persisted applications from the database.
     *
     * @return a list of [ApplicationModel] instances stored in the local database.
     */
    override suspend fun loadApplications(): List<ApplicationModel> {
        return databaseSource.getAll().map(ApplicationDatabaseMapper.toModel::map)
    }

    /**
     * Persists an application to the local database.
     *
     * @param application the [ApplicationModel] to save.
     */
    override suspend fun addApplication(application: ApplicationModel) {
        databaseSource.save(application.let(ApplicationDatabaseMapper.toResource::map))
    }

    /**
     * Removes an application from the local database.
     *
     * @param application the [ApplicationModel] to delete.
     */
    override suspend fun removeApplication(application: ApplicationModel) {
        databaseSource.delete(application.let(ApplicationDatabaseMapper.toResource::map))
    }

    /**
     * Observes changes to the persisted applications in the database.
     *
     * @return a [Flow] emitting updated lists of [ApplicationModel] whenever the database changes.
     */
    override fun observeApplications(): Flow<List<ApplicationModel>> {
        return databaseSource.observe().map {
            it.map(ApplicationDatabaseMapper.toModel::map)
        }
    }

    /**
     * Retrieves all system-installed applications from the platform.
     *
     * @return a list of [ApplicationModel] instances representing installed apps on the device.
     */
    override suspend fun getApplications(): List<ApplicationModel> {
        return platformSource.getApplications().map(ApplicationPlatformMapper.toModel::map)
    }

    /**
     * Retrieves a specific system-installed application by its package name.
     *
     * @param packageName the package name to look up.
     * @return the matching [ApplicationModel], or `null` if not found.
     */
    override suspend fun getApplication(packageName: String): ApplicationModel? {
        return platformSource.getApplication(packageName)?.let(ApplicationPlatformMapper.toModel::map)
    }
}
