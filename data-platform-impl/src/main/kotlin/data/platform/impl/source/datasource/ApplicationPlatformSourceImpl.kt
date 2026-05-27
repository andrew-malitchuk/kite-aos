package data.platform.impl.source.datasource

import android.content.Context
import android.content.pm.PackageManager
import data.platform.api.source.datasource.ApplicationPlatformSource
import data.platform.api.source.resource.ApplicationPlatform
import data.platform.impl.core.mapper.ApplicationSystemPlatformMapper
import org.koin.core.annotation.Single

/**
 * Android implementation of [ApplicationPlatformSource] using the system's [PackageManager].
 *
 * This implementation handles the heavy lifting of querying the Android package system,
 * filtering results to only include user-launchable apps, and mapping them into
 * the platform-agnostic [ApplicationPlatform] model.
 *
 * @param context The Android [Context] used to obtain the [PackageManager].
 * @see ApplicationPlatformSource
 * @see ApplicationSystemPlatformMapper
 * @since 0.0.1
 */
@Single(binds = [ApplicationPlatformSource::class])
internal class ApplicationPlatformSourceImpl(
    private val context: Context,
) : ApplicationPlatformSource {
    /**
     * Queries all installed applications and filters for those with a launch intent.
     *
     * The process involves:
     * 1. Retrieving all applications via [PackageManager.getInstalledApplications].
     * 2. Checking each package for a valid launch intent using [PackageManager.getLaunchIntentForPackage].
     * 3. Mapping `ApplicationInfo` to [ApplicationPlatform] and loading the display label.
     * 4. Sorting the final list alphabetically by name.
     *
     * @return A list of [ApplicationPlatform] for all launchable apps, sorted alphabetically by name.
     */
    override suspend fun getApplications(): List<ApplicationPlatform> {
        val packageManager = context.packageManager
        val installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return installedApplications
            // Keep only apps that declare a launcher intent (i.e., appear in the app drawer).
            .filter { info ->
                packageManager.getLaunchIntentForPackage(info.packageName) != null
            }
            // Map system ApplicationInfo to the domain model and fill in the display name,
            // which the mapper intentionally leaves empty (requires PackageManager to resolve).
            .map { info ->
                ApplicationSystemPlatformMapper.toPlatform.map(info).copy(
                    name = info.loadLabel(packageManager).toString(),
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    /**
     * Retrieves metadata for a single application by its package name.
     *
     * @param packageName The unique Android package identifier (e.g., "com.example.app").
     * @return The [ApplicationPlatform] for the given package, or `null` if the package is not installed.
     */
    override suspend fun getApplication(packageName: String): ApplicationPlatform? {
        val packageManager = context.packageManager

        return try {
            val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)

            ApplicationSystemPlatformMapper.toPlatform.map(info).copy(
                name = info.loadLabel(packageManager).toString(),
            )
        } catch (_: PackageManager.NameNotFoundException) {
            // Package not found on the device; return null as specified by the API contract.
            null
        }
    }
}
