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

    /**
     * Establishes a connection to the MQTT broker with the given credentials.
     *
     * @param server the MQTT broker hostname or IP address.
     * @param port the MQTT broker port number.
     * @param clientId the unique client identifier for this connection.
     * @param username the authentication username.
     * @param password the authentication password.
     * @param friendlyName a human-readable name for this device in MQTT discovery.
     */
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

    /**
     * Disconnects from the MQTT broker.
     */
    override suspend fun disconnect() {
        telemetryMqttSource.disconnect()
    }

    /**
     * Publishes a motion detection event to the MQTT broker.
     *
     * @param isDetected `true` if motion was detected, `false` otherwise.
     */
    override suspend fun sendMotion(isDetected: Boolean) {
        telemetryMqttSource.sendMotion(isDetected)
    }

    /**
     * Publishes the current battery level to the MQTT broker.
     *
     * @param level the battery level percentage (0-100).
     */
    override suspend fun sendBatteryLevel(level: Int) {
        telemetryMqttSource.sendBatteryLevel(level)
    }

    /**
     * Observes changes to the stored MQTT configuration.
     *
     * @return a [Flow] emitting the current [MqttModel] whenever the configuration changes,
     *   or `null` if not yet configured.
     */
    override fun observeMqttConfiguration(): Flow<MqttModel?> =
        mqttPreferenceSource.observeData().map { it?.let(MqttPreferenceMapper.toModel::map) }

    /**
     * Retrieves the current MQTT broker configuration.
     *
     * @return the stored [MqttModel], or `null` if not yet configured.
     */
    override suspend fun getMqttConfiguration(): MqttModel? =
        mqttPreferenceSource.getData()?.let(MqttPreferenceMapper.toModel::map)

    /**
     * Persists the given MQTT broker configuration.
     *
     * @param configuration the [MqttModel] containing broker connection details to store.
     */
    override suspend fun setMqttConfiguration(configuration: MqttModel) {
        mqttPreferenceSource.setData(configuration.let(MqttPreferenceMapper.toResource::map))
    }
}
