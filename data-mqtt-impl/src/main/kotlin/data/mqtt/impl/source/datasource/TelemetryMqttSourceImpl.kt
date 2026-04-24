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
 * This implementation handles reconnection logic, Home Assistant MQTT Discovery registration,
 * and publishing telemetry data for motion and battery states. The connection is maintained
 * via a long-running coroutine that periodically steps the MQTT client and attempts
 * reconnection on failure.
 *
 * @see TelemetryMqttSource
 * @see DeviceConfigMqtt
 * @see BatteryConfigMqtt
 * @since 0.0.1
 */
@Single(binds = [TelemetryMqttSource::class])
internal class TelemetryMqttSourceImpl : TelemetryMqttSource {
    /** The underlying MQTT client instance; `null` when disconnected or after a connection error. */
    private var client: MQTTClient? = null

    /** Background coroutine job that drives the MQTT client loop and reconnection. */
    private var connectionJob: Job? = null

    /** Flag controlling the reconnection loop; set to `false` to gracefully stop. */
    private var isConnecting = false

    /** The current client identifier, used as a topic prefix. `null` when not connected. */
    private var clientId: String? = null

    /** JSON serializer configured to include default values in serialized payloads. */
    private val json =
        Json {
            // Ensures default fields (e.g., device_class, unit_of_measurement) are included
            // in discovery payloads so Home Assistant receives the full configuration.
            encodeDefaults = true
        }

    private companion object {
        /**
         * Delay between reconnection attempts in milliseconds (5 seconds).
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
        // Tear down any existing connection before establishing a new one.
        disconnect()

        this.clientId = clientId

        connectionJob =
            CoroutineScope(Dispatchers.IO).launch {
                isConnecting = true
                // Reconnection loop: continuously attempts to maintain a connection.
                while (isConnecting) {
                    try {
                        // Create a new client only if there is no active one.
                        if (client == null || client?.isRunning() == false) {
                            client =
                                MQTTClient(
                                    mqttVersion = MQTTVersion.MQTT5,
                                    address = server,
                                    clientId = clientId,
                                    port = port,
                                    tls = null,
                                    userName = username,
                                    // kmqtt requires the password as a UByteArray.
                                    password = password.encodeToByteArray().toUByteArray(),
                                    debugLog = false,
                                    // Inbound messages are not used; provide a no-op handler.
                                    publishReceived = { _ -> },
                                )

                            // Register Home Assistant discovery configs on each fresh connection.
                            registerMotion(clientId = clientId, friendlyName = friendlyName)
                            registerBattery(clientId = clientId, friendlyName = friendlyName)
                        }

                        // Drive the MQTT client's internal network processing.
                        client?.step()
                    } catch (e: Exception) {
                        // On any error, discard the client so the next iteration creates a fresh one.
                        client = null
                    }
                    delay(RECONNECT_DELAY)
                }
            }
    }

    override suspend fun disconnect(): Unit = withContext(Dispatchers.IO) {
        // Signal the reconnection loop to stop.
        isConnecting = false
        connectionJob?.cancelAndJoin()
        connectionJob = null

        try {
            client?.disconnect(ReasonCode.SUCCESS)
        } catch (_: Exception) {
            // Swallow disconnect errors; the connection may already be broken.
        }

        client = null
        this@TelemetryMqttSourceImpl.clientId = null
    }

    override suspend fun sendMotion(isDetected: Boolean): Unit = withContext(Dispatchers.IO) {
        // Early return if not connected (clientId is null when disconnected).
        val currentClientId = clientId ?: return@withContext
        val topic = "${currentClientId}_motion/motion/state"
        // Home Assistant expects "ON"/"OFF" for binary sensor payloads.
        val payload = if (isDetected) "ON" else "OFF"
        publish(false, topic, payload)
    }

    override suspend fun sendBatteryLevel(level: Int): Unit = withContext(Dispatchers.IO) {
        // Early return if not connected (clientId is null when disconnected).
        val currentClientId = clientId ?: return@withContext
        val topic = "${currentClientId}_battery/battery/state"
        // Battery level is published as a plain integer string (e.g., "85").
        publish(false, topic, level.toString())
    }

    /**
     * Publishes a string [payload] to the specified [topic].
     *
     * Uses [Qos.AT_MOST_ONCE] (fire-and-forget) for all telemetry messages to minimise
     * latency. Publication errors are silently swallowed; the reconnection loop will
     * re-establish the connection if needed.
     *
     * @param retain Whether the message should be retained by the broker.
     * @param topic The MQTT topic to publish to.
     * @param payload The message payload as a string.
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    private suspend fun publish(retain: Boolean, topic: String, payload: String): Unit = withContext(Dispatchers.IO) {
        val mqttClient = client
        // Guard: skip publishing when the client is absent or not running.
        if (mqttClient == null || !mqttClient.isRunning()) {
            return@withContext
        }
        try {
            mqttClient.publish(
                retain,
                Qos.AT_MOST_ONCE,
                topic,
                // kmqtt requires payload as UByteArray.
                payload.encodeToByteArray().toUByteArray(),
            )
        } catch (_: Exception) {
            // Silently ignore publish failures; the reconnection loop handles recovery.
        }
    }

    /**
     * Registers a binary sensor for motion detection with Home Assistant via MQTT Discovery.
     *
     * Publishes a retained [DeviceConfigMqtt] configuration payload to the Home Assistant
     * discovery topic so the device appears automatically in the UI.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     * @see DeviceConfigMqtt
     */
    private suspend fun registerMotion(clientId: String, friendlyName: String) {
        // Home Assistant discovery topic format: homeassistant/<component>/<object_id>/config
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
        // Retained so that Home Assistant discovers the sensor even after a broker restart.
        publish(true, topic, payload)
    }

    /**
     * Registers a sensor for battery level with Home Assistant via MQTT Discovery.
     *
     * Publishes a retained [BatteryConfigMqtt] configuration payload to the Home Assistant
     * discovery topic so the battery sensor appears automatically in the UI.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     * @see BatteryConfigMqtt
     */
    private suspend fun registerBattery(clientId: String, friendlyName: String) {
        // Home Assistant discovery topic format: homeassistant/<component>/<object_id>/config
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
        // Retained so that Home Assistant discovers the sensor even after a broker restart.
        publish(true, batteryTopic, payload)
    }
}
