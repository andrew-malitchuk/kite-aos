package data.mqtt.impl.source.datasource

import data.mqtt.api.source.datasource.TelemetryMqttSource
import data.mqtt.impl.source.resources.BatteryConfigMqtt
import data.mqtt.impl.source.resources.BrightnessConfigMqtt
import data.mqtt.impl.source.resources.DeviceConfigMqtt
import data.mqtt.impl.source.resources.DeviceMqtt
import data.mqtt.impl.source.resources.ScreenConfigMqtt
import data.mqtt.impl.source.resources.UrlConfigMqtt
import data.mqtt.impl.source.resources.VolumeConfigMqtt
import io.github.davidepianca98.MQTTClient
import io.github.davidepianca98.mqtt.MQTTVersion
import io.github.davidepianca98.mqtt.Subscription
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.ReasonCode
import io.github.davidepianca98.mqtt.packets.mqttv5.SubscriptionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

/**
 * Implementation of [TelemetryMqttSource] using an internal [MQTTClient].
 *
 * This implementation handles reconnection logic, Home Assistant MQTT Discovery registration,
 * publishing telemetry data for motion, battery, volume, brightness, URL, and screen state,
 * as well as subscribing to inbound command topics and routing them via [commandFlow].
 *
 * On each fresh connection the client:
 * 1. Registers all HA discovery configs (motion, battery, volume, brightness, url, screen).
 * 2. Subscribes to command topics (volume/set, brightness/set, screen/set, app/launch).
 * 3. Routes inbound PUBLISH packets to [commandFlow] for consumption by [MqttService].
 *
 * @see TelemetryMqttSource
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

    /**
     * Shared flow that emits each inbound MQTT command as (topic, payload).
     *
     * Buffer capacity of 64 ensures commands are not dropped even if the collector is
     * temporarily suspended. [BufferOverflow.DROP_OLDEST] evicts the oldest unprocessed
     * command rather than blocking the MQTT step loop.
     */
    private val commandFlow = MutableSharedFlow<Pair<String, String>>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** JSON serializer configured to include default values in serialized payloads. */
    private val json = Json {
        // Ensures default fields (e.g., device_class, unit_of_measurement) are included
        // in discovery payloads so Home Assistant receives the full configuration.
        encodeDefaults = true
    }

    private companion object {
        /** Delay between reconnection attempts in milliseconds (5 seconds). */
        private const val RECONNECT_DELAY = 5000L
    }

    @OptIn(ExperimentalUnsignedTypes::class)
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
                                    // Route inbound PUBLISH packets to the command flow.
                                    publishReceived = { packet ->
                                        val payloadStr = packet.payload
                                            ?.toByteArray()
                                            ?.decodeToString()
                                            ?: ""
                                        commandFlow.tryEmit(Pair(packet.topicName, payloadStr))
                                    },
                                )

                            // Register Home Assistant discovery configs on each fresh connection.
                            registerMotion(clientId = clientId, friendlyName = friendlyName)
                            registerBattery(clientId = clientId, friendlyName = friendlyName)
                            registerVolume(clientId = clientId, friendlyName = friendlyName)
                            registerBrightness(clientId = clientId, friendlyName = friendlyName)
                            registerUrl(clientId = clientId, friendlyName = friendlyName)
                            registerScreen(clientId = clientId, friendlyName = friendlyName)

                            // Subscribe to inbound command topics so HA can control the device.
                            subscribeToCommandTopics(clientId = clientId)
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
        val currentClientId = clientId ?: return@withContext
        val topic = "${currentClientId}_motion/motion/state"
        // Home Assistant expects "ON"/"OFF" for binary sensor payloads.
        val payload = if (isDetected) "ON" else "OFF"
        publish(false, topic, payload)
    }

    override suspend fun sendBatteryLevel(level: Int): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        val topic = "${currentClientId}_battery/battery/state"
        // Battery level is published as a plain integer string (e.g., "85").
        publish(false, topic, level.toString())
    }

    override suspend fun sendVolume(level: Int): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        publish(false, "${currentClientId}_volume/volume/state", level.toString())
    }

    override suspend fun sendBrightness(level: Int): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        publish(false, "${currentClientId}_brightness/brightness/state", level.toString())
    }

    override suspend fun sendUrl(url: String): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        publish(false, "${currentClientId}_url/url/state", url)
    }

    override suspend fun sendScreenState(isOn: Boolean): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        val payload = if (isOn) "ON" else "OFF"
        publish(false, "${currentClientId}_screen/screen/state", payload)
    }

    override suspend fun sendWatchdogState(state: String): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        publish(false, "${currentClientId}_watchdog/state", state)
    }

    override suspend fun sendNetworkState(isOnline: Boolean): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        val payload = if (isOnline) "online" else "offline"
        publish(false, "${currentClientId}_network/state", payload)
    }

    override fun observeCommands(): Flow<Pair<String, String>> = commandFlow.asSharedFlow()

    /**
     * Subscribes to all inbound command topics on the broker.
     *
     * Called once per fresh connection, after all discovery configs are registered.
     * Subscriptions use [Qos.AT_MOST_ONCE] (fire-and-forget) for minimal latency.
     *
     * @param clientId The current client identifier used as a topic prefix.
     */
    private fun subscribeToCommandTopics(clientId: String) {
        val options = SubscriptionOptions(Qos.AT_MOST_ONCE)
        client?.subscribe(
            listOf(
                Subscription("${clientId}_volume/volume/set", options),
                Subscription("${clientId}_brightness/brightness/set", options),
                Subscription("${clientId}_screen/screen/set", options),
                Subscription("${clientId}_app/app/launch", options),
            ),
        )
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

    // region Home Assistant Discovery Registration

    /**
     * Registers a binary sensor for motion detection with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerMotion(clientId: String, friendlyName: String) {
        val topic = "homeassistant/binary_sensor/${clientId}_motion/config"
        val config = DeviceConfigMqtt(
            device = DeviceMqtt(name = friendlyName, identifiers = listOf(clientId)),
            uniqueId = "${clientId}_motion",
            stateTopic = "${clientId}_motion/motion/state",
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a sensor for battery level with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerBattery(clientId: String, friendlyName: String) {
        val topic = "homeassistant/sensor/${clientId}_battery/config"
        val config = BatteryConfigMqtt(
            device = DeviceMqtt(name = friendlyName, identifiers = listOf(clientId)),
            uniqueId = "${clientId}_battery",
            stateTopic = "${clientId}_battery/battery/state",
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a number entity for media volume control with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerVolume(clientId: String, friendlyName: String) {
        val topic = "homeassistant/number/${clientId}_volume/config"
        val config = VolumeConfigMqtt(
            device = DeviceMqtt(name = friendlyName, identifiers = listOf(clientId)),
            uniqueId = "${clientId}_volume",
            stateTopic = "${clientId}_volume/volume/state",
            commandTopic = "${clientId}_volume/volume/set",
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a number entity for screen brightness control with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerBrightness(clientId: String, friendlyName: String) {
        val topic = "homeassistant/number/${clientId}_brightness/config"
        val config = BrightnessConfigMqtt(
            device = DeviceMqtt(name = friendlyName, identifiers = listOf(clientId)),
            uniqueId = "${clientId}_brightness",
            stateTopic = "${clientId}_brightness/brightness/state",
            commandTopic = "${clientId}_brightness/brightness/set",
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a sensor entity for the current WebView URL with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerUrl(clientId: String, friendlyName: String) {
        val topic = "homeassistant/sensor/${clientId}_url/config"
        val config = UrlConfigMqtt(
            device = DeviceMqtt(name = friendlyName, identifiers = listOf(clientId)),
            uniqueId = "${clientId}_url",
            stateTopic = "${clientId}_url/url/state",
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a switch entity for screen on/off control with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerScreen(clientId: String, friendlyName: String) {
        val topic = "homeassistant/switch/${clientId}_screen/config"
        val config = ScreenConfigMqtt(
            device = DeviceMqtt(name = friendlyName, identifiers = listOf(clientId)),
            uniqueId = "${clientId}_screen",
            stateTopic = "${clientId}_screen/screen/state",
            commandTopic = "${clientId}_screen/screen/set",
        )
        publish(true, topic, json.encodeToString(config))
    }

    // endregion
}
