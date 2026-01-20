package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.DashboardPreferenceSource
import data.preferences.api.source.resource.DashboardPreference
import data.preferences.impl.core.mapper.DashboardProtobufPreferenceMapper
import data.preferences.impl.proto.DashboardDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.DashboardPreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [DashboardPreferenceSource::class])
internal class DashboardPreferenceSourceImpl(
    storage: DashboardPreferenceStorage,
) : BasePreferenceSourceImpl<DashboardDataProto.DashboardProtoModel, DashboardPreference>(
    storage = storage,
    mapper = DashboardProtobufPreferenceMapper,
), DashboardPreferenceSource