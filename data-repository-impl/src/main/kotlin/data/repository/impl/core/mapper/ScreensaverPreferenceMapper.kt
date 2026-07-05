package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ScreensaverPreference
import domain.core.source.model.ScreensaverModel
import domain.core.source.model.ScreensaverSource

/**
 * Mapper for converting between [ScreensaverModel] and [ScreensaverPreference].
 *
 * Handles bidirectional mapping between the domain representation of screensaver configuration
 * and the preference storage resource. The [ScreensaverSource] enum is encoded as an integer
 * in preferences: `0` maps to [ScreensaverSource.BLACK] and `1` maps to [ScreensaverSource.LOCAL_FOLDER].
 *
 * Unlike other preference mappers, this object does not implement [ModelResourceMapper] because
 * [ScreensaverModel] is not backed by a type that directly implements [data.core.source.resource.Resource].
 *
 * @see ScreensaverModel
 * @see ScreensaverPreference
 * @see ScreensaverSource
 * @since 0.0.1
 */
internal object ScreensaverPreferenceMapper {

    // NOTE: source is stored as an integer in preferences (0 = BLACK, 1 = LOCAL_FOLDER).
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
