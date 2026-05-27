package data.platform.api.source.datasource

import data.platform.api.source.resource.ApplicationPlatform

/**
 * Data source interface for interacting with the Android system to retrieve application metadata.
 *
 * This source is primarily used to discover which applications are installed on the device
 * and can be launched by the user.
 *
 * @see data.platform.impl.source.datasource.ApplicationPlatformSourceImpl
 * @see ApplicationPlatform
 * @since 0.0.1
 */
public interface ApplicationPlatformSource {
    /**
     * Retrieves a list of all applications that have a launch intent defined in their manifest.
     *
     * This effectively returns the "launcher apps" that a user would normally see in their app drawer.
     *
     * @return A list of [ApplicationPlatform] resources, typically sorted alphabetically.
     */
    public suspend fun getApplications(): List<ApplicationPlatform>

    /**
     * Retrieves metadata for a specific application identified by its [packageName].
     *
     * @param packageName The unique Android package identifier (e.g., "com.example.app").
     * @return The [ApplicationPlatform] resource containing the name, package, and icon, or `null` if not found.
     */
    public suspend fun getApplication(packageName: String): ApplicationPlatform?
}
