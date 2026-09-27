package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for how user interaction with the kiosk is interpreted.
 *
 * Covers two independent behaviours that both hinge on detecting deliberate input: returning the
 * dashboard to its home URL after a period of inactivity, and opening the control drawer from a
 * hardware volume-button gesture when no on-screen affordance is available.
 *
 * @property inactivityResetMinutes Minutes of inactivity before returning to the home URL;
 *   `0` or `null` disables the feature.
 * @property volumeGestureEnabled Whether repeated volume-button presses open the control drawer.
 * @property volumeGesturePressCount Presses required to trigger the gesture
 *   (`null`/`0` = [DEFAULT_PRESS_COUNT]).
 *
 * @see Model
 * @since 2.2.0
 */
public data class InteractionModel(
    val inactivityResetMinutes: Int? = null,
    val volumeGestureEnabled: Boolean? = null,
    val volumeGesturePressCount: Int? = null,
) : Model {

    /** `true` when a non-zero idle timeout is configured. */
    public val isInactivityResetOn: Boolean get() = (inactivityResetMinutes ?: 0) > 0

    /** `true` only when the user explicitly enabled the volume gesture. */
    public val isVolumeGestureOn: Boolean get() = volumeGestureEnabled == true

    /**
     * Presses required for the gesture, never below [MIN_PRESS_COUNT].
     *
     * The floor matters: a gesture of one or two presses would fire during ordinary volume
     * adjustment and make the hardware keys unusable for their actual purpose.
     */
    public val pressCountOrDefault: Int
        get() = (volumeGesturePressCount ?: 0).takeIf { it >= MIN_PRESS_COUNT } ?: DEFAULT_PRESS_COUNT

    /**
     * Companion defaults for [InteractionModel].
     *
     * @since 2.2.0
     */
    public companion object {
        /** Default number of volume presses that open the control drawer. */
        public const val DEFAULT_PRESS_COUNT: Int = 5

        /** Smallest press count that cannot be hit by ordinary volume adjustment. */
        public const val MIN_PRESS_COUNT: Int = 3
    }
}
