package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.ScreensaverPreferenceSource
import data.preferences.api.source.resource.ScreensaverPreference
import data.preferences.impl.core.mapper.ScreensaverProtobufPreferenceMapper
import data.preferences.impl.proto.ScreensaverDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.ScreensaverPreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [ScreensaverPreferenceSource::class])
internal class ScreensaverPreferenceSourceImpl(
    storage: ScreensaverPreferenceStorage,
) : BasePreferenceSourceImpl<ScreensaverDataProto.ScreensaverProtoModel, ScreensaverPreference>(
    storage = storage,
    mapper = ScreensaverProtobufPreferenceMapper,
),
    ScreensaverPreferenceSource
