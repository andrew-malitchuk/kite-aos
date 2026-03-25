package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.DockPositionPreferenceSource
import data.preferences.api.source.resource.DockPreference
import data.preferences.impl.core.mapper.DockProtobufPreferenceMapper
import data.preferences.impl.proto.DockDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.DockPreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [DockPositionPreferenceSource::class])
internal class DockPositionPreferenceSourceImpl(
    storage: DockPreferenceStorage,
) : BasePreferenceSourceImpl<DockDataProto.DockProtoModel, DockPreference>(
    storage = storage,
    mapper = DockProtobufPreferenceMapper,
),
    DockPositionPreferenceSource
