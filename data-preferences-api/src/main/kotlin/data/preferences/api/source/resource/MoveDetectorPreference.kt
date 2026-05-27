package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing motion detector configuration.
 *
 * Controls the CameraX-based presence detection service behavior, including sensitivity
 * thresholds and timing for screen dimming, timeout, and FAB visibility.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message MoveDetectorPreference {
 *     optional bool enabled = 1;
 *     optional int32 sensitivity = 2;
 *     optional int64 dim_delay = 3;
 *     optional int64 screen_timeout = 4;
 *     optional int64 fab_delay = 5;
 * }
 * ```
 *
 * @property enabled whether the motion detector is enabled.
 * @property sensitivity the sensitivity threshold for motion detection.
 * @property dimDelay the delay in milliseconds before the screen dims due to inactivity.
 * @property screenTimeout the delay in milliseconds before the screen turns off due to inactivity.
 * @property fabDelay the delay in milliseconds before the Floating Action Button is hidden.
 *
 * @see data.preferences.api.source.datasource.MoveDetectorPreferenceSource
 *
 * @since 0.0.1
 */
public data class MoveDetectorPreference(
    val enabled: Boolean? = null,
    val sensitivity: Int? = null,
    val dimDelay: Long? = null,
    val screenTimeout: Long? = null,
    val fabDelay: Long? = null,
) : Resource
