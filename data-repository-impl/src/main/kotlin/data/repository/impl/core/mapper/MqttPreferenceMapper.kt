package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.MqttPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.MqttModel

/**
 * Mapper for converting between [MqttModel] and [MqttPreference].
 */
internal object MqttPreferenceMapper : ModelResourceMapper<MqttModel, MqttPreference> {
    override val toResource: Mapper<MqttModel, MqttPreference> =
        Mapper { model ->
            MqttPreference(
                ip = model.ip,
                port = model.port,
                clientId = model.clientId,
                username = model.username,
                password = model.password,
                enabled = model.enabled,
                friendlyName = model.friendlyName,
            )
        }
    override val toModel: Mapper<MqttPreference, MqttModel> =
        Mapper { preference ->
            MqttModel(
                ip = preference.ip,
                port = preference.port,
                clientId = preference.clientId,
                username = preference.username,
                password = preference.password,
                enabled = preference.enabled,
                friendlyName = preference.friendlyName,
            )
        }
}
