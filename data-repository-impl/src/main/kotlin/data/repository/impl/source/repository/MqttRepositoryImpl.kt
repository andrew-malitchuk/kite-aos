package data.repository.impl.source.repository

import data.mqtt.api.source.datasource.TelemetryMqttSource
import data.preferences.api.source.datasource.MqttPreferenceSource
import data.repository.impl.core.mapper.MqttPreferenceMapper
import domain.core.source.model.MqttModel
import domain.repository.api.source.repository.MqttRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Implementation of [MqttRepository] that delegates to [TelemetryMqttSource].
 * This acts as a bridge between the domain and data layers for MQTT operations.
 */
/**
 * Implementation of [MqttRepository] that delegates to [TelemetryMqttSource] and [MqttPreferenceSource].
 *
 * @property telemetryMqttSource The MQTT telemetry source for network operations.
 * @property mqttPreferenceSource The preference source for storing MQTT configuration.
 */
@Single(binds = [MqttRepository::class])
internal class MqttRepositoryImpl(
    private val telemetryMqttSource: TelemetryMqttSource,
    private val mqttPreferenceSource: MqttPreferenceSource
) : MqttRepository {
    override suspend fun connect(
        server: String,
        port: Int,
        clientId: String,
        username: String,
        password: String,
        friendlyName: String
    ) {
        telemetryMqttSource.connect(server, port, clientId, username, password,  friendlyName)
    }

    override suspend fun disconnect() {
        telemetryMqttSource.disconnect()
    }

    override suspend fun sendMotion(isDetected: Boolean) {
        telemetryMqttSource.sendMotion(isDetected)
    }

    override suspend fun sendBatteryLevel(level: Int) {
        telemetryMqttSource.sendBatteryLevel(level)
    }

    override fun observeMqttConfiguration(): Flow<MqttModel?> =
        mqttPreferenceSource.observeData().map { it?.let(MqttPreferenceMapper.toModel::map) }

    override suspend fun getMqttConfiguration(): MqttModel? =
        mqttPreferenceSource.getData()?.let(MqttPreferenceMapper.toModel::map)


    override suspend fun setMqttConfiguration(configuration: MqttModel) {
        mqttPreferenceSource.setData(configuration.let(MqttPreferenceMapper.toResource::map))
    }
}
