package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for the unattended-operation safeguards.
 *
 * A wall-mounted dashboard is judged on uptime, and each flag here exists because a specific class
 * of Android behaviour will otherwise take the panel down silently, overnight, with nobody
 * watching. Every field is nullable and resolves to a documented default when absent, so a config
 * written before these settings existed keeps the safeguards on rather than silently losing them.
 *
 * @property crashRelaunchEnabled Whether a fatal crash schedules a relaunch (`null` = enabled).
 * @property scheduledReloadEnabled Whether the dashboard reloads once a day (`null` = disabled).
 * @property scheduledReloadHour Hour of day (0–23) for the daily reload (`null` = [DEFAULT_RELOAD_HOUR]).
 * @property memoryRecoveryEnabled Whether memory pressure triggers a reload (`null` = enabled).
 * @property connectionMonitorEnabled Whether the WebView pauses while the backend is down
 *   (`null` = enabled).
 * @property wifiLockEnabled Whether a WifiLock holds the radio out of power-save
 *   (`null` = enabled).
 *
 * @see Model
 * @since 2.2.0
 */
public data class ResilienceModel(
    val crashRelaunchEnabled: Boolean? = null,
    val scheduledReloadEnabled: Boolean? = null,
    val scheduledReloadHour: Int? = null,
    val memoryRecoveryEnabled: Boolean? = null,
    val connectionMonitorEnabled: Boolean? = null,
    val wifiLockEnabled: Boolean? = null,
) : Model {

    /** `true` unless the user explicitly turned crash relaunch off. */
    public val isCrashRelaunchOn: Boolean get() = crashRelaunchEnabled != false

    /** `true` only when the user explicitly turned the daily reload on. */
    public val isScheduledReloadOn: Boolean get() = scheduledReloadEnabled == true

    /** `true` unless the user explicitly turned memory recovery off. */
    public val isMemoryRecoveryOn: Boolean get() = memoryRecoveryEnabled != false

    /** `true` unless the user explicitly turned the connection monitor off. */
    public val isConnectionMonitorOn: Boolean get() = connectionMonitorEnabled != false

    /** `true` unless the user explicitly turned the WiFi lock off. */
    public val isWifiLockOn: Boolean get() = wifiLockEnabled != false

    /** The configured daily-reload hour, falling back to [DEFAULT_RELOAD_HOUR]. */
    public val reloadHourOrDefault: Int get() = scheduledReloadHour ?: DEFAULT_RELOAD_HOUR

    /**
     * Companion defaults for [ResilienceModel].
     *
     * @since 2.2.0
     */
    public companion object {
        /**
         * Default hour for the daily reload.
         *
         * 04:00 is deliberately after the small hours when a panel is least likely to be watched,
         * and clear of the 03:00 slot the auto-reboot feature defaults to, so the two do not fight.
         */
        public const val DEFAULT_RELOAD_HOUR: Int = 4
    }
}
