package data.platform.impl.core.mapper.base

import common.core.core.mapper.Mapper
import data.core.source.resource.Resource

/**
 * Base interface for mapping native Android system objects to the project's platform resources.
 *
 * This abstraction ensures a consistent pattern for bridging raw system data (from services
 * like `ConnectivityManager` or `PackageManager`) into structured [Resource] models used by
 * the rest of the data layer.
 *
 * @param SYSTEM The raw Android system type (e.g., `ApplicationInfo`).
 * @param PLATFORM The target platform resource type defined in the API module.
 * @see data.platform.impl.core.mapper.ApplicationSystemPlatformMapper
 * @since 0.0.1
 */
internal interface SystemPlatformMapper<SYSTEM : Any, PLATFORM : Resource> {
    /**
     * A [Mapper] that performs the transformation from [SYSTEM] to [PLATFORM].
     *
     * @return a [Mapper] instance capable of converting [SYSTEM] into [PLATFORM].
     */
    public val toPlatform: Mapper<SYSTEM, PLATFORM>
}
