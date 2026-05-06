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
 * Implementation of [MqttRepository] that delegates to [TelemetryMqttSource] and [MqttPreferenceSource].
 *
 * This repository acts as a bridge between the domain and data layers for MQTT operations,
 * coordinating between the MQTT network client for telemetry operations and the preference
 * store for persisting MQTT broker configuration.
 *
 * @property telemetryMqttSource The MQTT telemetry source for network operations (connect, disconnect, publish).
 * @property mqttPreferenceSource The preference source for storing and retrieving MQTT configuration.
 * @see MqttRepository
 * @see MqttPreferenceMapper
 * @since 0.0.1
 */
@Single(binds = [MqttRepository::class])
internal class MqttRepositoryImpl(
    private val telemetryMqttSource: TelemetryMqttSource,
    private val mqttPreferenceSource: MqttPreferenceSource,
) : MqttRepository {

    override suspend fun connect(
        server: String,
        port: Int,
        clientId: String,
        username: String,
        password: String,
        friendlyName: String,
    ) {
        telemetryMqttSource.connect(server, port, clientId, username, password, friendlyName)
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

    override suspend fun sendVolume(level: Int) {
        telemetryMqttSource.sendVolume(level)
    }

    override suspend fun sendBrightness(level: Int) {
        telemetryMqttSource.sendBrightness(level)
    }

    override suspend fun sendUrl(url: String) {
        telemetryMqttSource.sendUrl(url)
    }

    override suspend fun sendScreenState(isOn: Boolean) {
        telemetryMqttSource.sendScreenState(isOn)
    }

    override fun observeCommands(): Flow<Pair<String, String>> =
        telemetryMqttSource.observeCommands()

    override fun observeMqttConfiguration(): Flow<MqttModel?> =
        mqttPreferenceSource.observeData().map { it?.let(MqttPreferenceMapper.toModel::map) }

    override suspend fun getMqttConfiguration(): MqttModel? =
        mqttPreferenceSource.getData()?.let(MqttPreferenceMapper.toModel::map)

    override suspend fun setMqttConfiguration(configuration: MqttModel) {
        mqttPreferenceSource.setData(configuration.let(MqttPreferenceMapper.toResource::map))
    }
}
