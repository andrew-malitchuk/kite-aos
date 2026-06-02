package data.platform.impl.source.datasource

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import data.platform.api.source.datasource.ApplicationPlatformSource
import data.platform.api.source.resource.ApplicationPlatform
import data.platform.impl.core.mapper.ApplicationSystemPlatformMapper
import org.koin.core.annotation.Single

/**
 * Android implementation of [ApplicationPlatformSource] using the system's [PackageManager].
 *
 * Uses [PackageManager.queryIntentActivities] with MAIN+LAUNCHER to enumerate launchable apps
 * without requiring QUERY_ALL_PACKAGES — the manifest's <queries> block grants the necessary
 * package visibility on Android 11+.
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
     * Returns all apps that appear in the device launcher, sorted alphabetically.
     */
    override suspend fun getApplications(): List<ApplicationPlatform> {
        val packageManager = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        @Suppress("QueryPermissionsNeeded")
        return packageManager.queryIntentActivities(launcherIntent, 0)
            .map { resolveInfo ->
                ApplicationPlatform(
                    name = resolveInfo.loadLabel(packageManager).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = resolveInfo.activityInfo.applicationInfo.icon,
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
