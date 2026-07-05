package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model for the screensaver feature configuration.
 *
 * Controls when and how the screensaver activates after a period of inactivity.
 * All properties are nullable to support partial updates.
 *
 * @property enabled Whether the screensaver is active.
 * @property activationDelay The idle time in milliseconds before the screensaver activates.
 * @property slideInterval The interval in milliseconds between image slides when using [ScreensaverSource.LOCAL_FOLDER].
 * @property showClock Whether an overlay clock is displayed during the screensaver.
 * @property source The content source for the screensaver.
 * @property localFolderUri The URI of the local folder to use when [source] is [ScreensaverSource.LOCAL_FOLDER].
 *
 * @see ScreensaverSource
 * @see Model
 * @since 0.0.1
 */
public data class ScreensaverModel(
    val enabled: Boolean?,
    val activationDelay: Long?,
    val slideInterval: Long?,
    val showClock: Boolean?,
    val source: ScreensaverSource?,
    val localFolderUri: String?,
) : Model

/**
 * Defines the content source used by the screensaver.
 *
 * @since 0.0.1
 */
public enum class ScreensaverSource {
    /** Displays a plain black screen with no content. */
    BLACK,

    /** Cycles through images from a user-selected local folder. */
    LOCAL_FOLDER,
}
