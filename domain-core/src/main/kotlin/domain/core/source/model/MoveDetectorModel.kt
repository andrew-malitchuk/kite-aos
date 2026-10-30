package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for the motion detector settings.
 *
 * @property enabled Whether the motion detector is enabled.
 * @property sensitivity The sensitivity threshold for motion detection.
 * @property dimDelay The delay in milliseconds before the screen dims due to inactivity.
 * @property screenTimeout The delay in milliseconds before the screen turns off due to inactivity.
 * @property fabDelay The delay in milliseconds before the Floating Action Button is hidden.
 * @property quietHoursEnabled Whether motion-triggered wake is suppressed during the configured
 *   nightly window. Dimming/locking on inactivity still runs as normal; only the camera motion
 *   signal is ignored.
 * @property quietHoursStartHour Hour (0–23) the quiet-hours window starts.
 * @property quietHoursStartMinute Minute (0–59) the quiet-hours window starts.
 * @property quietHoursEndHour Hour (0–23) the quiet-hours window ends.
 * @property quietHoursEndMinute Minute (0–59) the quiet-hours window ends.
 *
 * @see Model
 * @since 0.0.1
 */
public data class MoveDetectorModel(
    val enabled: Boolean?,
    val sensitivity: Int?,
    val dimDelay: Long?,
    val screenTimeout: Long?,
    val fabDelay: Long?,
    val quietHoursEnabled: Boolean? = null,
    val quietHoursStartHour: Int? = null,
    val quietHoursStartMinute: Int? = null,
    val quietHoursEndHour: Int? = null,
    val quietHoursEndMinute: Int? = null,
) : Model
