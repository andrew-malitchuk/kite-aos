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
 */
public data class MoveDetectorModel(
    val enabled: Boolean?,
    val sensitivity: Int?,
    val dimDelay: Long?,
    val screenTimeout: Long?,
    val fabDelay: Long?,
) : Model
