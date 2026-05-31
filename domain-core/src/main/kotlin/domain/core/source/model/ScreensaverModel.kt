package domain.core.source.model

import domain.core.source.model.base.Model

public data class ScreensaverModel(
    val enabled: Boolean?,
    val activationDelay: Long?,
    val slideInterval: Long?,
    val showClock: Boolean?,
    val source: ScreensaverSource?,
    val localFolderUri: String?,
) : Model

public enum class ScreensaverSource {
    BLACK,
    LOCAL_FOLDER,
}
