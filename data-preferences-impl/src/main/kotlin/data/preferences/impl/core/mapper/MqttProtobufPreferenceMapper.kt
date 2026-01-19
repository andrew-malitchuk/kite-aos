package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.MqttPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.MqttDataProto

internal object MqttProtobufPreferenceMapper :
    ProtobufPreferenceMapper<MqttDataProto.MqttProtoModel, MqttPreference> {
    override val toProtobuf: Mapper<MqttPreference, MqttDataProto.MqttProtoModel> =
        Mapper { input ->
            MqttDataProto.MqttProtoModel.newBuilder()
                .setIp(input.ip ?: "")
                .setPort(input.port ?: "")
                .setClientId(input.clientId ?: "")
                .setUsername(input.username ?: "")
                .setPassword(input.password ?: "")
                .setEnabled(input.enabled ?: false)
                .setFriendlyName(input.friendlyName ?: "")
                .build()
        }

    override val toPreference: Mapper<MqttDataProto.MqttProtoModel, MqttPreference> =
        Mapper { input ->
            MqttPreference(
                ip = input.ip,
                port = input.port,
                clientId = input.clientId,
                username = input.username,
                password = input.password,
                enabled = input.enabled,
                friendlyName = input.friendlyName
            )
        }
}
