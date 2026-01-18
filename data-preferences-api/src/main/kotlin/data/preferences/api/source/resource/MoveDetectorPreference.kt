package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference model for the motion detector settings.
 *
 * @property enabled Whether the motion detector is enabled.
 * @property sensitivity The sensitivity threshold for motion detection.
 * @property dimDelay The delay in milliseconds before the screen dims due to inactivity.
 * @property screenTimeout The delay in milliseconds before the screen turns off due to inactivity.
 * @property fabDelay The delay in milliseconds before the Floating Action Button is hidden.
 */
public data class MoveDetectorPreference(
    val enabled: Boolean? = null,
    val sensitivity: Int? = null,
    val dimDelay: Long? = null,
    val screenTimeout: Long? = null,
    val fabDelay: Long? = null
) : Resource
