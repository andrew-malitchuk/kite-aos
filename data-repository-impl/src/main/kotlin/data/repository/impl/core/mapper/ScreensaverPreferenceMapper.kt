package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ScreensaverPreference
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.ScreensaverSource

internal object ScreensaverPreferenceMapper {

    val toModel: Mapper<ScreensaverPreference, ScreensaverModel> = Mapper { input ->
        ScreensaverModel(
            enabled = input.enabled,
            activationDelay = input.activationDelay,
            slideInterval = input.slideInterval,
            showClock = input.showClock,
            source = when (input.source) {
                1 -> ScreensaverSource.LOCAL_FOLDER
                else -> ScreensaverSource.BLACK
            },
            localFolderUri = input.localFolderUri,
        )
    }

    val toResource: Mapper<ScreensaverModel, ScreensaverPreference> = Mapper { input ->
        ScreensaverPreference(
            enabled = input.enabled,
            activationDelay = input.activationDelay,
            slideInterval = input.slideInterval,
            showClock = input.showClock,
            source = when (input.source) {
                ScreensaverSource.LOCAL_FOLDER -> 1
                else -> 0
            },
            localFolderUri = input.localFolderUri,
        )
    }
}
