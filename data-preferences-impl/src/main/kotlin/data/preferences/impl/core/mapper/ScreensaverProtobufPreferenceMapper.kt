package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ScreensaverPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.ScreensaverDataProto

/**
 * Bidirectional mapper between [ScreensaverDataProto.ScreensaverProtoModel] and [ScreensaverPreference].
 *
 * Handles conversion of screensaver configuration (enabled state, activation delay, slide interval,
 * clock visibility, source type, and local folder URI) between the Protobuf serialization format
 * used by Proto DataStore and the preference resource exposed to the data layer API.
 *
 * Null values are replaced with safe defaults when writing to Protobuf: `false` for booleans,
 * `60L` for activationDelay, `30L` for slideInterval, `true` for showClock, `0` for source,
 * and empty string for localFolderUri. On reading, an empty string URI is mapped back to `null`.
 *
 * @see ScreensaverPreference
 * @see ScreensaverDataProto.ScreensaverProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.ScreensaverPreferenceSourceImpl
 * @since 0.0.1
 */
internal object ScreensaverProtobufPreferenceMapper :
    ProtobufPreferenceMapper<ScreensaverDataProto.ScreensaverProtoModel, ScreensaverPreference> {

    /** Converts a [ScreensaverPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<ScreensaverPreference, ScreensaverDataProto.ScreensaverProtoModel> =
        Mapper { input ->
            ScreensaverDataProto.ScreensaverProtoModel.newBuilder()
                .setEnabled(input.enabled ?: false)
                .setActivationDelay(input.activationDelay ?: 60L)
                .setSlideInterval(input.slideInterval ?: 30L)
                .setShowClock(input.showClock ?: true)
                .setSource(input.source ?: 0)
                .setLocalFolderUri(input.localFolderUri ?: "")
                .build()
        }

    /** Converts a Protobuf [ScreensaverDataProto.ScreensaverProtoModel] back to a [ScreensaverPreference]. */
    override val toPreference: Mapper<ScreensaverDataProto.ScreensaverProtoModel, ScreensaverPreference> =
        Mapper { input ->
            ScreensaverPreference(
                enabled = input.enabled,
                activationDelay = input.activationDelay,
                slideInterval = input.slideInterval,
                showClock = input.showClock,
                source = input.source,
                localFolderUri = input.localFolderUri.ifEmpty { null },
            )
        }
}
