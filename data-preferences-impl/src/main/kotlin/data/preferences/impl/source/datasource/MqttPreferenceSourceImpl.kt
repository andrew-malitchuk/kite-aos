package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.MqttPreferenceSource
import data.preferences.api.source.resource.MqttPreference
import data.preferences.impl.core.mapper.MqttProtobufPreferenceMapper
import data.preferences.impl.proto.MqttDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.MqttPreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [MqttPreferenceSource::class])
internal class MqttPreferenceSourceImpl(
    storage: MqttPreferenceStorage,
) : BasePreferenceSourceImpl<MqttDataProto.MqttProtoModel, MqttPreference>(
    storage = storage,
    mapper = MqttProtobufPreferenceMapper,
), MqttPreferenceSource
