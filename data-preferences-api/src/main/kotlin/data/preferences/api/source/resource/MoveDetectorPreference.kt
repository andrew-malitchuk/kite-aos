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
 *     optional bool quiet_hours_enabled = 6;
 *     optional int32 quiet_hours_start_hour = 7;
 *     optional int32 quiet_hours_start_minute = 8;
 *     optional int32 quiet_hours_end_hour = 9;
 *     optional int32 quiet_hours_end_minute = 10;
 * }
 * ```
 *
 * @property enabled whether the motion detector is enabled.
 * @property sensitivity the sensitivity threshold for motion detection.
 * @property dimDelay the delay in milliseconds before the screen dims due to inactivity.
 * @property screenTimeout the delay in milliseconds before the screen turns off due to inactivity.
 * @property fabDelay the delay in milliseconds before the Floating Action Button is hidden.
 * @property quietHoursEnabled whether motion-triggered wake is suppressed during the configured
 *   nightly window.
 * @property quietHoursStartHour hour (0–23) the quiet-hours window starts.
 * @property quietHoursStartMinute minute (0–59) the quiet-hours window starts.
 * @property quietHoursEndHour hour (0–23) the quiet-hours window ends.
 * @property quietHoursEndMinute minute (0–59) the quiet-hours window ends.
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
    val quietHoursEnabled: Boolean? = null,
    val quietHoursStartHour: Int? = null,
    val quietHoursStartMinute: Int? = null,
    val quietHoursEndHour: Int? = null,
    val quietHoursEndMinute: Int? = null,
) : Resource
