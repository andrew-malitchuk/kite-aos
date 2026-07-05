package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the reduce motion / disable animations setting.
 *
 * @property isEnabled `true` when animations should be disabled across the UI.
 * @since 0.0.6
 */
public data class ReduceMotionPreference(
    val isEnabled: Boolean? = null,
) : Resource
