package data.platform.api.source.resource

import data.core.source.resource.Resource

/**
 * Platform-agnostic representation of an Android application's metadata.
 *
 * This model is used by the presentation layer to display application information
 * in the kiosk dashboard and selection menus.
 *
 * @property name The localized, human-readable label of the application (e.g., "Settings").
 * @property packageName The unique system identifier for the app package (e.g., "com.android.settings").
 * @property icon The resource ID or reference used to load the application's icon from the system.
 */
public data class ApplicationPlatform(
    val name: String,
    val packageName: String,
    val icon: Int,
) : Resource
