package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource for inactivity reset and the volume-button gesture.
 *
 * @property inactivityResetMinutes minutes of inactivity before returning to the home URL; `0` disables.
 * @property volumeGestureEnabled whether repeated volume presses open the control drawer.
 * @property volumeGesturePressCount presses required to trigger the gesture, or `null` if unset.
 *
 * @see data.preferences.api.source.datasource.InteractionPreferenceSource
 * @since 2.2.0
 */
public data class InteractionPreference(
    val inactivityResetMinutes: Int? = null,
    val volumeGestureEnabled: Boolean? = null,
    val volumeGesturePressCount: Int? = null,
) : Resource
