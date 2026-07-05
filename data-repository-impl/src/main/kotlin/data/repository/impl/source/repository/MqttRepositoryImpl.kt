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
     * Connects to the MQTT broker via [telemetryMqttSource].
     *
     * @param server The broker address (e.g., `"tcp://192.168.1.100"`).
     * @param port The broker port (e.g., `1883`).
     * @param clientId Unique client identifier for this device on the broker.
     * @param username Username for broker authentication.
     * @param password Password for broker authentication.
     * @param friendlyName Human-readable device name used in Home Assistant discovery payloads.
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
     * Disconnects from the MQTT broker and releases associated resources via [telemetryMqttSource].
     */
    override suspend fun disconnect() {
        telemetryMqttSource.disconnect()
    }

    /**
     * Publishes the current motion detection state to the MQTT broker.
     *
     * @param isDetected `true` if motion is currently detected, `false` otherwise.
     */
    override suspend fun sendMotion(isDetected: Boolean) {
        telemetryMqttSource.sendMotion(isDetected)
    }

    /**
     * Publishes the current battery level to the MQTT broker.
     *
     * @param level Current battery percentage (0–100).
     */
    override suspend fun sendBatteryLevel(level: Int) {
        telemetryMqttSource.sendBatteryLevel(level)
    }

    /**
     * Publishes the current media volume level to the MQTT broker.
     *
     * @param level Volume level as a percentage (0–100).
     */
    override suspend fun sendVolume(level: Int) {
        telemetryMqttSource.sendVolume(level)
    }

    /**
     * Publishes the current screen brightness to the MQTT broker.
     *
     * @param level Brightness value (0–255, matching Android's brightness scale).
     */
    override suspend fun sendBrightness(level: Int) {
        telemetryMqttSource.sendBrightness(level)
    }

    /**
     * Publishes the URL of the currently displayed WebView page to the MQTT broker.
     *
     * @param url The currently loaded page URL.
     */
    override suspend fun sendUrl(url: String) {
        telemetryMqttSource.sendUrl(url)
    }

    /**
     * Publishes the current screen power state to the MQTT broker.
     *
     * @param isOn `true` if the screen is on and interactive, `false` if it is off or locked.
     */
    override suspend fun sendScreenState(isOn: Boolean) {
        telemetryMqttSource.sendScreenState(isOn)
    }

    /**
     * Publishes the current watchdog health state to the MQTT broker.
     *
     * @param state One of `"ok"`, `"fail(N)"`, or `"recovering"`.
     */
    override suspend fun sendWatchdogState(state: String) {
        telemetryMqttSource.sendWatchdogState(state)
    }

    /**
     * Publishes the device network connectivity state to the MQTT broker.
     *
     * @param isOnline `true` when network is available, `false` when lost.
     */
    override suspend fun sendNetworkState(isOnline: Boolean) {
        telemetryMqttSource.sendNetworkState(isOnline)
    }

    /**
     * Publishes the MJPEG camera stream URL to the MQTT broker.
     *
     * @param url The full stream URL, or an empty string when streaming is inactive.
     */
    override suspend fun sendCameraUrl(url: String) {
        telemetryMqttSource.sendCameraUrl(url)
    }

    /**
     * Returns a [Flow] of inbound MQTT command messages from [telemetryMqttSource].
     *
     * @return a [Flow] emitting (topic, payload) pairs for all subscribed command topics.
     */
    override fun observeCommands(): Flow<Pair<String, String>> = telemetryMqttSource.observeCommands()

    /**
     * Observes changes to the MQTT configuration stored in [mqttPreferenceSource].
     *
     * @return a [Flow] emitting the current [MqttModel] on every configuration change,
     *   or `null` if the configuration has not yet been set.
     */
    override fun observeMqttConfiguration(): Flow<MqttModel?> =
        mqttPreferenceSource.observeData().map { it?.let(MqttPreferenceMapper.toModel::map) }

    /**
     * Retrieves the current MQTT configuration from [mqttPreferenceSource].
     *
     * @return the current [MqttModel], or `null` if not yet configured.
     */
    override suspend fun getMqttConfiguration(): MqttModel? =
        mqttPreferenceSource.getData()?.let(MqttPreferenceMapper.toModel::map)

    /**
     * Persists the provided MQTT configuration to [mqttPreferenceSource].
     *
     * @param configuration The [MqttModel] to save.
     */
    override suspend fun setMqttConfiguration(configuration: MqttModel) {
        mqttPreferenceSource.setData(configuration.let(MqttPreferenceMapper.toResource::map))
    }
}
