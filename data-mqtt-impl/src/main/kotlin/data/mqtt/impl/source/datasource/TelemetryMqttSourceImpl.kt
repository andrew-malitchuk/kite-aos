package data.mqtt.impl.source.datasource

import data.mqtt.api.source.datasource.TelemetryMqttSource
import data.mqtt.impl.source.resources.BatteryConfigMqtt
import data.mqtt.impl.source.resources.DeviceConfigMqtt
import data.mqtt.impl.source.resources.DeviceMqtt
import io.github.davidepianca98.MQTTClient
import io.github.davidepianca98.mqtt.MQTTVersion
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.ReasonCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

/**
 * Implementation of [TelemetryMqttSource] using an internal [MQTTClient].
 *
 * This implementation handles reconnection logic, Home Assistant discovery registration,
 * and publishing telemetry data for motion and battery states.
 */
@Single(binds = [TelemetryMqttSource::class])
internal class TelemetryMqttSourceImpl : TelemetryMqttSource {
    private var client: MQTTClient? = null
    private var connectionJob: Job? = null
    private var isConnecting = false
    private var clientId: String? = null

    private val json =
        Json {
            encodeDefaults = true
        }

    private companion object {
        /**
         * Delay between reconnection attempts in milliseconds.
         */
        private const val RECONNECT_DELAY = 5000L
    }

    override suspend fun connect(
        server: String,
        port: Int,
        clientId: String,
        username: String,
        password: String,
        friendlyName: String,
    ) {
        disconnect()

        this.clientId = clientId

        connectionJob =
            CoroutineScope(Dispatchers.IO).launch {
                isConnecting = true
                while (isConnecting) {
                    try {
                        if (client == null || client?.isRunning() == false) {
                            client =
                                MQTTClient(
                                    mqttVersion = MQTTVersion.MQTT5,
                                    address = server,
                                    clientId = clientId,
                                    port = port,
                                    tls = null,
                                    userName = username,
                                    password = password.encodeToByteArray().toUByteArray(),
                                    debugLog = false,
                                    publishReceived = { _ -> },
                                )

                            registerMotion(clientId = clientId, friendlyName = friendlyName)
                            registerBattery(clientId = clientId, friendlyName = friendlyName)
                        }

                        client?.step()
                    } catch (e: Exception) {
                        client = null
                    }
                    delay(RECONNECT_DELAY)
                }
            }
    }

    override suspend fun disconnect(): Unit = withContext(Dispatchers.IO) {
        isConnecting = false
        connectionJob?.cancelAndJoin()
        connectionJob = null

        try {
            client?.disconnect(ReasonCode.SUCCESS)
        } catch (_: Exception) {
        }

        client = null
        this@TelemetryMqttSourceImpl.clientId = null
    }

    override suspend fun sendMotion(isDetected: Boolean): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        val topic = "${currentClientId}_motion/motion/state"
        val payload = if (isDetected) "ON" else "OFF"
        publish(false, topic, payload)
    }

    override suspend fun sendBatteryLevel(level: Int): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        val topic = "${currentClientId}_battery/battery/state"
        publish(false, topic, level.toString())
    }

    /**
     * Publishes a string [payload] to the specified [topic].
     *
     * @param retain Whether the message should be retained by the broker.
     * @param topic The MQTT topic to publish to.
     * @param payload The message payload as a string.
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    private suspend fun publish(retain: Boolean, topic: String, payload: String): Unit = withContext(Dispatchers.IO) {
        val mqttClient = client
        if (mqttClient == null || !mqttClient.isRunning()) {
            return@withContext
        }
        try {
            mqttClient.publish(
                retain,
                Qos.AT_MOST_ONCE,
                topic,
                payload.encodeToByteArray().toUByteArray(),
            )
        } catch (_: Exception) {
        }
    }

    /**
     * Registers a binary sensor for motion detection with Home Assistant.
     *
     * @param clientId Unique identifier for the device.
     * @param friendlyName Human-readable name for the device.
     */
    private suspend fun registerMotion(clientId: String, friendlyName: String) {
        val topic = "homeassistant/binary_sensor/${clientId}_motion/config"
        val config =
            DeviceConfigMqtt(
                device =
                DeviceMqtt(
                    name = friendlyName,
                    identifiers = listOf(clientId),
                ),
                uniqueId = "${clientId}_motion",
                stateTopic = "${clientId}_motion/motion/state",
            )

        val payload = json.encodeToString(config)
        publish(true, topic, payload)
    }

    /**
     * Registers a sensor for battery level with Home Assistant.
     *
     * @param clientId Unique identifier for the device.
     * @param friendlyName Human-readable name for the device.
     */
    private suspend fun registerBattery(clientId: String, friendlyName: String) {
        val batteryTopic = "homeassistant/sensor/${clientId}_battery/config"
        val batteryConfig =
            BatteryConfigMqtt(
                device =
                DeviceMqtt(
                    name = friendlyName,
                    identifiers = listOf(clientId),
                ),
                uniqueId = "${clientId}_battery",
                stateTopic = "${clientId}_battery/battery/state",
            )

        val payload = json.encodeToString(batteryConfig)
        publish(true, batteryTopic, payload)
    }
}
