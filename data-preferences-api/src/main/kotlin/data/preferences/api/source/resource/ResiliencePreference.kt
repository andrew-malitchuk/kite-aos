package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource for the unattended-operation safeguards.
 *
 * Fields are stated positively here; the Protobuf layer stores the default-on flags inverted and
 * the reload hour offset by one, because proto3 scalars have no field presence. See
 * `resilience_data.proto` and [data.preferences.impl.core.mapper.ResilienceProtobufPreferenceMapper].
 *
 * @property crashRelaunchEnabled whether a fatal crash schedules a relaunch of the kiosk.
 * @property scheduledReloadEnabled whether the dashboard reloads once a day.
 * @property scheduledReloadHour hour of day (0–23) for the daily reload, or `null` if unset.
 * @property memoryRecoveryEnabled whether memory pressure triggers a deferred reload.
 * @property connectionMonitorEnabled whether the WebView pauses while the backend is unreachable.
 * @property wifiLockEnabled whether a WifiLock keeps the radio out of power-save.
 *
 * @see data.preferences.api.source.datasource.ResiliencePreferenceSource
 * @since 2.2.0
 */
public data class ResiliencePreference(
    val crashRelaunchEnabled: Boolean? = null,
    val scheduledReloadEnabled: Boolean? = null,
    val scheduledReloadHour: Int? = null,
    val memoryRecoveryEnabled: Boolean? = null,
    val connectionMonitorEnabled: Boolean? = null,
    val wifiLockEnabled: Boolean? = null,
) : Resource
