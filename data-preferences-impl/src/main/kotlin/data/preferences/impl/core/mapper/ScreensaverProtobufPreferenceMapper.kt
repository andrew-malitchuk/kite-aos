package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ScreensaverPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.ScreensaverDataProto

internal object ScreensaverProtobufPreferenceMapper :
    ProtobufPreferenceMapper<ScreensaverDataProto.ScreensaverProtoModel, ScreensaverPreference> {

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
