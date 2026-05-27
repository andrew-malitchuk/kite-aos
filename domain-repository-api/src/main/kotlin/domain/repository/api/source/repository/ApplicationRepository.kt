package domain.repository.api.source.repository

import domain.core.source.model.ApplicationModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing application-related data.
 *
 * @see ApplicationModel
 * @see data.repository.impl.source.repository.ApplicationRepositoryImpl
 * @since 0.0.1
 */
public interface ApplicationRepository {
    /**
     * Loads the list of applications saved in the local database.
     *
     * @return All persisted [ApplicationModel] entries.
     * @since 0.0.1
     */
    public suspend fun loadApplications(): List<ApplicationModel>

    /**
     * Adds an [application] to the local database.
     *
     * @param application The application to persist.
     * @since 0.0.1
     */
    public suspend fun addApplication(application: ApplicationModel)

    /**
     * Removes an [application] from the local database.
     *
     * @param application The application to delete.
     * @since 0.0.1
     */
    public suspend fun removeApplication(application: ApplicationModel)

    /**
     * Observes changes to the applications stored in the local database.
     *
     * @return A [Flow] emitting the current list of persisted applications on every change.
     * @since 0.0.1
     */
    public fun observeApplications(): Flow<List<ApplicationModel>>

    /**
     * Retrieves the list of all launcher-enabled applications installed on the platform.
     *
     * @return All launchable applications from the system package manager.
     * @since 0.0.1
     */
    public suspend fun getApplications(): List<ApplicationModel>

    /**
     * Retrieves details for a specific application by its [packageName] from the platform.
     *
     * @param packageName The Android package name to look up.
     * @return The matching [ApplicationModel], or null if the package is not found.
     * @since 0.0.1
     */
    public suspend fun getApplication(packageName: String): ApplicationModel?
}
