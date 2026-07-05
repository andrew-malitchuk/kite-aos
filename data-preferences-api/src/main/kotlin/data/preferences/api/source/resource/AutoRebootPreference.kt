package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the automatic device reboot schedule.
 *
 * Stores the configuration for a recurring reboot job used to keep the kiosk device
 * healthy over long uptime periods.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message AutoRebootProtoModel {
 *     bool enabled = 1;
 *     int32 hour = 2;
 *     int32 minute = 3;
 *     int32 interval_days = 4;
 * }
 * ```
 *
 * @property enabled whether scheduled automatic rebooting is active.
 * @property hour the hour of the day (0–23) at which the reboot should occur.
 * @property minute the minute of the hour (0–59) at which the reboot should occur.
 * @property intervalDays the recurrence interval in days (e.g., 1 = daily, 7 = weekly).
 *
 * @see data.preferences.api.source.datasource.AutoRebootPreferenceSource
 *
 * @since 0.0.1
 */
public data class AutoRebootPreference(
    val enabled: Boolean? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val intervalDays: Int? = null,
) : Resource
