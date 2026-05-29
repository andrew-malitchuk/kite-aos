package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.AutoRebootPreferenceSource
import data.preferences.api.source.resource.AutoRebootPreference
import data.preferences.impl.core.mapper.AutoRebootProtobufPreferenceMapper
import data.preferences.impl.proto.AutoRebootDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.AutoRebootPreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [AutoRebootPreferenceSource::class])
internal class AutoRebootPreferenceSourceImpl(
    storage: AutoRebootPreferenceStorage,
) : BasePreferenceSourceImpl<AutoRebootDataProto.AutoRebootProtoModel, AutoRebootPreference>(
    storage = storage,
    mapper = AutoRebootProtobufPreferenceMapper,
),
    AutoRebootPreferenceSource
