package data.platform.impl.core.mapper

import android.content.pm.ApplicationInfo
import common.core.core.mapper.Mapper
import data.platform.api.source.resource.ApplicationPlatform
import data.platform.impl.core.mapper.base.SystemPlatformMapper

/**
 * Mapper responsible for converting the system's [ApplicationInfo] into the API's [ApplicationPlatform] model.
 *
 * This mapper acts as a bridge between the raw Android framework data and the application's
 * clean data layer. Note that the application name is usually loaded separately using
 * the [PackageManager] as it requires a system call.
 */
public object ApplicationSystemPlatformMapper :
    SystemPlatformMapper<ApplicationInfo, ApplicationPlatform> {

    override val toPlatform: Mapper<ApplicationInfo, ApplicationPlatform> =
        Mapper { input ->
            ApplicationPlatform(
                name = "",
                packageName = input.packageName,
                icon = input.icon
            )
        }
}