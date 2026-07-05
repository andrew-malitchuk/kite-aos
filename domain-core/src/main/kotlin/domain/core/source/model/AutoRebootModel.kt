package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing the automatic device reboot schedule.
 *
 * When enabled, the device will reboot at the specified time on a repeating interval.
 * All properties are nullable to allow partial updates without overwriting existing values.
 *
 * @property enabled Whether the auto-reboot scheduler is active.
 * @property hour The hour of the day (0–23) at which the reboot should occur.
 * @property minute The minute of the hour (0–59) at which the reboot should occur.
 * @property intervalDays The number of days between automatic reboots (e.g., 1 for daily, 7 for weekly).
 *
 * @see Model
 * @since 0.0.1
 */
public data class AutoRebootModel(
    val enabled: Boolean? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val intervalDays: Int? = null,
) : Model
