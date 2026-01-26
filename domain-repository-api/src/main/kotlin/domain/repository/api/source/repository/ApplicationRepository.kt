package domain.repository.api.source.repository

import domain.core.source.model.ApplicationModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing application-related data.
 */
public interface ApplicationRepository {
    /**
     * Loads the list of applications saved in the local database.
     */
    public suspend fun loadApplications(): List<ApplicationModel>

    /**
     * Adds an [application] to the local database.
     */
    public suspend fun addApplication(application: ApplicationModel)

    /**
     * Removes an [application] from the local database.
     */
    public suspend fun removeApplication(application: ApplicationModel)

    /**
     * Observes changes to the applications stored in the local database.
     */
    public fun observeApplications(): Flow<List<ApplicationModel>>

    /**
     * Retrieves the list of all launcher-enabled applications installed on the platform.
     */
    public suspend fun getApplications(): List<ApplicationModel>

    /**
     * Retrieves details for a specific application by its [packageName] from the platform.
     */
    public suspend fun getApplication(packageName: String): ApplicationModel?
}