package data.mqtt.impl.source.datasource

import data.mqtt.api.source.datasource.TelemetryMqttSource
import data.mqtt.impl.source.resources.AppVersionConfigMqtt
import data.mqtt.impl.source.resources.BatteryConfigMqtt
import data.mqtt.impl.source.resources.BrightnessConfigMqtt
import data.mqtt.impl.source.resources.CameraUrlConfigMqtt
import data.mqtt.impl.source.resources.ClearCacheConfigMqtt
import data.mqtt.impl.source.resources.CommandButtonConfigMqtt
import data.mqtt.impl.source.resources.DashboardConfigMqtt
import data.mqtt.impl.source.resources.DeviceConfigMqtt
import data.mqtt.impl.source.resources.DeviceMqtt
import data.mqtt.impl.source.resources.FabConfigMqtt
import data.mqtt.impl.source.resources.IpAddressConfigMqtt
import data.mqtt.impl.source.resources.NavigateConfigMqtt
import data.mqtt.impl.source.resources.RamUsageConfigMqtt
import data.mqtt.impl.source.resources.ScreenConfigMqtt
import data.mqtt.impl.source.resources.ScreensaverConfigMqtt
import data.mqtt.impl.source.resources.UptimeConfigMqtt
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
 * publishing telemetry data for motion, battery, volume, brightness, URL, screen state and the
 * companion diagnostic sensors, as well as subscribing to inbound command topics and routing them
 * via [commandFlow].
 *
 * On each fresh connection the client:
 * 1. Registers an MQTT Last Will on the availability topic, so an ungraceful drop marks the panel
 *    unavailable in Home Assistant without anything having to notice the panel is gone.
 * 2. Registers the HA discovery configs appropriate to the form factor and the user's per-entity
 *    diagnostic opt-outs, and unregisters the ones that do not apply.
 * 3. Subscribes to command topics (volume/set, brightness/set, screen/set, app/launch, …).
 * 4. Publishes a retained `online` availability payload.
 * 5. Routes inbound PUBLISH packets to [commandFlow] for consumption by `MqttService`.
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

    // Device form-factor ("tv"/"tablet") reported in every HA discovery `device` block.
    private var deviceModel: String? = null

    // Ids of the diagnostic entities the user has left enabled. Publishes for entities absent
    // from this set are skipped, mirroring the fact that they were never registered.
    private var enabledDiagnostics: Set<String> = emptySet()

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

        /** Availability payload Home Assistant reads as "this device is reachable". */
        private const val PAYLOAD_ONLINE = "online"

        /** Availability payload Home Assistant reads as "this device is gone". */
        private const val PAYLOAD_OFFLINE = "offline"

        /**
         * Every (entity domain, unique-id suffix) pair this client can ever register.
         *
         * [purgeDiscovery] walks the full list rather than only the currently active entities:
         * a purge must also clear registrations left behind by an earlier form factor or by a
         * diagnostic entity the user has since switched off, otherwise those entities survive in
         * Home Assistant with nothing left to update them.
         */
        private val ALL_ENTITIES = listOf(
            "binary_sensor" to "motion",
            "binary_sensor" to "dashboard",
            "sensor" to "battery",
            "sensor" to "url",
            "sensor" to "camera_url",
            "sensor" to "uptime",
            "sensor" to "app_version",
            "sensor" to "ip_address",
            "sensor" to "ram_usage",
            "number" to "volume",
            "number" to "brightness",
            "switch" to "screen",
            "switch" to "fab",
            "switch" to "screensaver",
            "button" to "clear_cache",
            "button" to "reload",
            "button" to "navigate_home",
            "text" to "navigate",
        )
    }

    /**
     * Returns the device-wide availability topic for [clientId].
     *
     * Deliberately a single topic for the whole device rather than one per entity: Home Assistant
     * resolves availability per entity but every entity here shares one fate — the app is either
     * running and connected, or it is not.
     */
    private fun availabilityTopicFor(clientId: String): String = "$clientId/availability"

    /**
     * Returns the shared remote-command topic for [clientId].
     *
     * Sits directly under the client prefix rather than beside an entity (`{clientId}_x/x/set`)
     * because it belongs to no single entity: the discovery buttons and the text field all publish
     * their own command envelope onto this one topic, and so can a bare `mosquitto_pub`.
     */
    private fun commandTopicFor(clientId: String): String = "$clientId/command/set"

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun connect(
        server: String,
        port: Int,
        clientId: String,
        username: String,
        password: String,
        friendlyName: String,
        model: String,
        diagnostics: Set<String>,
    ) {
        // Tear down any existing connection before establishing a new one.
        disconnect()

        this.clientId = clientId
        this.deviceModel = model
        this.enabledDiagnostics = diagnostics

        val availabilityTopic = availabilityTopicFor(clientId)

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
                                    // Last Will: the broker publishes this if the connection drops
                                    // without a DISCONNECT — a crash, a kill, or the panel simply
                                    // losing power. This is what makes Home Assistant show the
                                    // device as unavailable instead of holding its last values
                                    // forever. Retained so a Home Assistant restart still sees it.
                                    //
                                    // Note: keepAlive is intentionally left at the library default
                                    // (60 s). kmqtt only sends PINGREQ inside the window between
                                    // 0.9x and 1.0x keepAlive, and `step()` below is polled every
                                    // RECONNECT_DELAY (5 s) — shortening keepAlive would narrow
                                    // that window below the poll interval, so the client would
                                    // skip the ping and trip its own keep-alive timeout instead.
                                    willTopic = availabilityTopic,
                                    willPayload = PAYLOAD_OFFLINE.encodeToByteArray().toUByteArray(),
                                    willRetain = true,
                                    willQos = Qos.AT_LEAST_ONCE,
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

                            // Several controls are not meaningful or not reliably controllable on
                            // Android TV, so their Home Assistant entities are hidden there to avoid
                            // surfacing controls that would silently fail:
                            //  - brightness: the panel backlight isn't exposed via Settings.System;
                            //  - screen on/off: lockNow() needs a Device Administrator, which
                            //    Android TV doesn't provide;
                            //  - volume: audio over HDMI/ARC is owned by the TV/receiver, so
                            //    AudioManager changes don't affect the actual output;
                            //  - battery: TV boxes have no battery.
                            val isTv = model == "tv"

                            registerEntities(
                                clientId = clientId,
                                friendlyName = friendlyName,
                                isTv = isTv,
                                diagnostics = diagnostics,
                            )

                            // Subscribe to inbound command topics so HA can control the device.
                            subscribeToCommandTopics(clientId = clientId, isTv = isTv)

                            // Announce availability only after discovery, so Home Assistant has the
                            // entity definitions in hand before it is told the device is reachable.
                            publish(true, availabilityTopic, PAYLOAD_ONLINE)
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
        // A DISCONNECT tells the broker this was intentional, which makes it drop the Last Will.
        // Publish the offline payload first so a deliberate shutdown still marks the panel
        // unavailable in Home Assistant rather than leaving it looking healthy.
        clientId?.let { publish(true, availabilityTopicFor(it), PAYLOAD_OFFLINE) }

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
        enabledDiagnostics = emptySet()
    }

    override suspend fun purgeDiscovery(): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        ALL_ENTITIES.forEach { (entityType, suffix) ->
            unregisterEntity(entityType = entityType, uniqueId = "${currentClientId}_$suffix")
        }
        // Clear the retained availability payload too. Leaving it behind would keep an `online`
        // (or `offline`) message on the broker for a device that no longer publishes anything.
        publish(true, availabilityTopicFor(currentClientId), "")
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

    override suspend fun sendCameraUrl(url: String): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        publish(false, "${currentClientId}_camera_url/camera_url/state", url)
    }

    override suspend fun sendDashboardState(isReachable: Boolean): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext
        val payload = if (isReachable) "online" else "offline"
        publish(false, "${currentClientId}_dashboard/dashboard/state", payload)
    }

    override suspend fun sendCompanionTelemetry(
        uptimeSeconds: Long,
        appVersion: String,
        ipAddress: String,
        ramUsagePercent: Int,
    ): Unit = withContext(Dispatchers.IO) {
        val currentClientId = clientId ?: return@withContext

        if (DIAGNOSTIC_UPTIME in enabledDiagnostics) {
            publish(false, "${currentClientId}_uptime/uptime/state", uptimeSeconds.toString())
        }
        // Blank values are skipped rather than published as empty strings: an empty sensor state
        // reads in Home Assistant as a real "unknown" reading, which is misleading when the only
        // problem is that the value was momentarily unresolvable (e.g. no IP while reassociating).
        if (DIAGNOSTIC_APP_VERSION in enabledDiagnostics && appVersion.isNotEmpty()) {
            publish(false, "${currentClientId}_app_version/app_version/state", appVersion)
        }
        if (DIAGNOSTIC_IP_ADDRESS in enabledDiagnostics && ipAddress.isNotEmpty()) {
            publish(false, "${currentClientId}_ip_address/ip_address/state", ipAddress)
        }
        if (DIAGNOSTIC_RAM_USAGE in enabledDiagnostics) {
            publish(false, "${currentClientId}_ram_usage/ram_usage/state", ramUsagePercent.toString())
        }
    }

    override fun observeCommands(): Flow<Pair<String, String>> = commandFlow.asSharedFlow()

    /**
     * Subscribes to all inbound command topics on the broker.
     *
     * Called once per fresh connection, after all discovery configs are registered.
     * Subscriptions use [Qos.AT_MOST_ONCE] (fire-and-forget) for minimal latency.
     *
     * @param clientId The current client identifier used as a topic prefix.
     * @param isTv When `true`, the volume, brightness and screen command topics are skipped
     *   because those entities are hidden on Android TV (see [connect]).
     */
    private fun subscribeToCommandTopics(clientId: String, isTv: Boolean) {
        val options = SubscriptionOptions(Qos.AT_MOST_ONCE)
        val subscriptions = mutableListOf(
            Subscription("${clientId}_app/app/launch", options),
            Subscription("${clientId}_fab/fab/set", options),
            Subscription("${clientId}_screensaver/screensaver/set", options),
            Subscription("${clientId}_clear_cache/clear_cache/press", options),
            // Shared remote-command channel. One topic for the whole vocabulary keeps an
            // automation from having to learn a separate topic per action.
            Subscription(commandTopicFor(clientId), options),
            // TV motion fallback: HA (e.g. a PIR sensor automation) publishes ON here to
            // inject presence when the device has no camera.
            Subscription("${clientId}_motion/motion/set", options),
        )
        if (!isTv) {
            subscriptions += Subscription("${clientId}_volume/volume/set", options)
            subscriptions += Subscription("${clientId}_brightness/brightness/set", options)
            subscriptions += Subscription("${clientId}_screen/screen/set", options)
        }
        client?.subscribe(subscriptions)
    }

    /**
     * Removes a Home Assistant discovery entity by publishing an empty retained payload to its
     * config topic. Used to hide entities unsupported on the current form factor (brightness and
     * screen on Android TV), to drop diagnostic entities the user has opted out of, and to clear
     * any stale registration left by a previous form factor.
     *
     * @param entityType The HA entity domain in the config topic (e.g. `"number"`, `"switch"`).
     * @param uniqueId The entity unique id / topic segment (e.g. `"${clientId}_brightness"`).
     */
    private suspend fun unregisterEntity(entityType: String, uniqueId: String) {
        publish(true, "homeassistant/$entityType/$uniqueId/config", "")
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
     * Registers every Home Assistant discovery entity that applies to this device, and
     * unregisters the ones that do not.
     *
     * Unregistering is as important as registering: MQTT Discovery configs are retained messages,
     * so an entity stays in Home Assistant until an empty payload replaces it. Without the
     * unregister branches, switching form factor or opting a diagnostic sensor out would leave a
     * permanently stale entity behind that the user has to delete by hand.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     * @param isTv Whether this is the Android TV form factor.
     * @param diagnostics Ids of the diagnostic entities the user has left enabled.
     */
    private suspend fun registerEntities(
        clientId: String,
        friendlyName: String,
        isTv: Boolean,
        diagnostics: Set<String>,
    ) {
        registerMotion(clientId = clientId, friendlyName = friendlyName)
        registerUrl(clientId = clientId, friendlyName = friendlyName)
        registerFab(clientId = clientId, friendlyName = friendlyName)
        registerScreensaver(clientId = clientId, friendlyName = friendlyName)
        registerCameraUrl(clientId = clientId, friendlyName = friendlyName)
        registerClearCache(clientId = clientId, friendlyName = friendlyName)
        registerRemoteCommands(clientId = clientId, friendlyName = friendlyName)
        registerDashboard(clientId = clientId, friendlyName = friendlyName)

        if (isTv) {
            // Clear any entity left over from a previous mobile registration.
            unregisterEntity(entityType = "number", uniqueId = "${clientId}_brightness")
            unregisterEntity(entityType = "switch", uniqueId = "${clientId}_screen")
            unregisterEntity(entityType = "number", uniqueId = "${clientId}_volume")
            unregisterEntity(entityType = "sensor", uniqueId = "${clientId}_battery")
        } else {
            registerBattery(clientId = clientId, friendlyName = friendlyName)
            registerVolume(clientId = clientId, friendlyName = friendlyName)
            registerBrightness(clientId = clientId, friendlyName = friendlyName)
            registerScreen(clientId = clientId, friendlyName = friendlyName)
        }

        registerDiagnostics(clientId = clientId, friendlyName = friendlyName, diagnostics = diagnostics)
    }

    /**
     * Registers the opted-in companion diagnostic sensors and unregisters the opted-out ones.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     * @param diagnostics Ids of the diagnostic entities the user has left enabled.
     */
    private suspend fun registerDiagnostics(clientId: String, friendlyName: String, diagnostics: Set<String>) {
        if (DIAGNOSTIC_UPTIME in diagnostics) {
            registerUptime(clientId = clientId, friendlyName = friendlyName)
        } else {
            unregisterEntity(entityType = "sensor", uniqueId = "${clientId}_$DIAGNOSTIC_UPTIME")
        }

        if (DIAGNOSTIC_APP_VERSION in diagnostics) {
            registerAppVersion(clientId = clientId, friendlyName = friendlyName)
        } else {
            unregisterEntity(entityType = "sensor", uniqueId = "${clientId}_$DIAGNOSTIC_APP_VERSION")
        }

        if (DIAGNOSTIC_IP_ADDRESS in diagnostics) {
            registerIpAddress(clientId = clientId, friendlyName = friendlyName)
        } else {
            unregisterEntity(entityType = "sensor", uniqueId = "${clientId}_$DIAGNOSTIC_IP_ADDRESS")
        }

        if (DIAGNOSTIC_RAM_USAGE in diagnostics) {
            registerRamUsage(clientId = clientId, friendlyName = friendlyName)
        } else {
            unregisterEntity(entityType = "sensor", uniqueId = "${clientId}_$DIAGNOSTIC_RAM_USAGE")
        }
    }

    /** Builds the shared HA discovery `device` block for [clientId] / [friendlyName]. */
    private fun deviceBlock(clientId: String, friendlyName: String): DeviceMqtt =
        DeviceMqtt(name = friendlyName, identifiers = listOf(clientId), model = deviceModel)

    /**
     * Registers a binary sensor for motion detection with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerMotion(clientId: String, friendlyName: String) {
        val topic = "homeassistant/binary_sensor/${clientId}_motion/config"
        val config = DeviceConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_motion",
            stateTopic = "${clientId}_motion/motion/state",
            availabilityTopic = availabilityTopicFor(clientId),
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
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_battery",
            stateTopic = "${clientId}_battery/battery/state",
            availabilityTopic = availabilityTopicFor(clientId),
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
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_volume",
            stateTopic = "${clientId}_volume/volume/state",
            commandTopic = "${clientId}_volume/volume/set",
            availabilityTopic = availabilityTopicFor(clientId),
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
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_brightness",
            stateTopic = "${clientId}_brightness/brightness/state",
            commandTopic = "${clientId}_brightness/brightness/set",
            availabilityTopic = availabilityTopicFor(clientId),
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
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_url",
            stateTopic = "${clientId}_url/url/state",
            availabilityTopic = availabilityTopicFor(clientId),
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
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_screen",
            stateTopic = "${clientId}_screen/screen/state",
            commandTopic = "${clientId}_screen/screen/set",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a switch entity for FAB visibility control with Home Assistant via MQTT Discovery.
     *
     * Uses `optimistic` mode — no state topic is needed because HA assumes the state matches
     * the last command without confirmation from the device.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerFab(clientId: String, friendlyName: String) {
        val topic = "homeassistant/switch/${clientId}_fab/config"
        val config = FabConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_fab",
            commandTopic = "${clientId}_fab/fab/set",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a switch entity for screensaver control with Home Assistant via MQTT Discovery.
     *
     * Uses `optimistic` mode — `"ON"` activates the screensaver, `"OFF"` dismisses it.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerScreensaver(clientId: String, friendlyName: String) {
        val topic = "homeassistant/switch/${clientId}_screensaver/config"
        val config = ScreensaverConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_screensaver",
            commandTopic = "${clientId}_screensaver/screensaver/set",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a sensor entity for the MJPEG camera stream URL with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerCameraUrl(clientId: String, friendlyName: String) {
        val topic = "homeassistant/sensor/${clientId}_camera_url/config"
        val config = CameraUrlConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_camera_url",
            stateTopic = "${clientId}_camera_url/camera_url/state",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers a button entity that flushes the WebView cache and reloads the dashboard.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerClearCache(clientId: String, friendlyName: String) {
        val topic = "homeassistant/button/${clientId}_clear_cache/config"
        val config = ClearCacheConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_clear_cache",
            commandTopic = "${clientId}_clear_cache/clear_cache/press",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers the Home Assistant entities that drive the shared remote-command topic.
     *
     * Only the three actions worth a permanent control surface get an entity: `reload` and
     * `navigate_home` as buttons, and `navigate` as a text field. The rest of the vocabulary
     * (`back`, `forward`, `evaluate_js`) is reachable on the same topic but deliberately has no
     * entity — those are scripting actions, and an entity per action would bury the panel's actual
     * controls under a wall of buttons nobody presses by hand.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerRemoteCommands(clientId: String, friendlyName: String) {
        val commandTopic = commandTopicFor(clientId)
        val device = deviceBlock(clientId, friendlyName)
        val availabilityTopic = availabilityTopicFor(clientId)

        val reload = CommandButtonConfigMqtt(
            device = device,
            name = "Reload",
            commandTopic = commandTopic,
            payloadPress = """{"action": "reload"}""",
            icon = "mdi:refresh",
            availabilityTopic = availabilityTopic,
            uniqueId = "${clientId}_reload",
        )
        publish(true, "homeassistant/button/${clientId}_reload/config", json.encodeToString(reload))

        val navigateHome = CommandButtonConfigMqtt(
            device = device,
            name = "Navigate Home",
            commandTopic = commandTopic,
            payloadPress = """{"action": "navigate_home"}""",
            icon = "mdi:home",
            availabilityTopic = availabilityTopic,
            uniqueId = "${clientId}_navigate_home",
        )
        publish(true, "homeassistant/button/${clientId}_navigate_home/config", json.encodeToString(navigateHome))

        val navigate = NavigateConfigMqtt(
            device = device,
            commandTopic = commandTopic,
            availabilityTopic = availabilityTopic,
            uniqueId = "${clientId}_navigate",
        )
        publish(true, "homeassistant/text/${clientId}_navigate/config", json.encodeToString(navigate))
    }

    /**
     * Registers the dashboard-reachability binary sensor with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerDashboard(clientId: String, friendlyName: String) {
        val topic = "homeassistant/binary_sensor/${clientId}_dashboard/config"
        val config = DashboardConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_dashboard",
            stateTopic = "${clientId}_dashboard/dashboard/state",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers the device uptime sensor with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerUptime(clientId: String, friendlyName: String) {
        val topic = "homeassistant/sensor/${clientId}_$DIAGNOSTIC_UPTIME/config"
        val config = UptimeConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_$DIAGNOSTIC_UPTIME",
            stateTopic = "${clientId}_$DIAGNOSTIC_UPTIME/$DIAGNOSTIC_UPTIME/state",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers the app version sensor with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerAppVersion(clientId: String, friendlyName: String) {
        val topic = "homeassistant/sensor/${clientId}_$DIAGNOSTIC_APP_VERSION/config"
        val config = AppVersionConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_$DIAGNOSTIC_APP_VERSION",
            stateTopic = "${clientId}_$DIAGNOSTIC_APP_VERSION/$DIAGNOSTIC_APP_VERSION/state",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers the LAN IP address sensor with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerIpAddress(clientId: String, friendlyName: String) {
        val topic = "homeassistant/sensor/${clientId}_$DIAGNOSTIC_IP_ADDRESS/config"
        val config = IpAddressConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_$DIAGNOSTIC_IP_ADDRESS",
            stateTopic = "${clientId}_$DIAGNOSTIC_IP_ADDRESS/$DIAGNOSTIC_IP_ADDRESS/state",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    /**
     * Registers the device-wide RAM usage sensor with Home Assistant via MQTT Discovery.
     *
     * @param clientId Unique identifier for the device, used as a topic prefix.
     * @param friendlyName Human-readable name for the device shown in Home Assistant.
     */
    private suspend fun registerRamUsage(clientId: String, friendlyName: String) {
        val topic = "homeassistant/sensor/${clientId}_$DIAGNOSTIC_RAM_USAGE/config"
        val config = RamUsageConfigMqtt(
            device = deviceBlock(clientId, friendlyName),
            uniqueId = "${clientId}_$DIAGNOSTIC_RAM_USAGE",
            stateTopic = "${clientId}_$DIAGNOSTIC_RAM_USAGE/$DIAGNOSTIC_RAM_USAGE/state",
            availabilityTopic = availabilityTopicFor(clientId),
        )
        publish(true, topic, json.encodeToString(config))
    }

    // endregion
}

/**
 * Diagnostic entity id for the uptime sensor.
 *
 * These mirror `domain.core.source.model.MqttDiagnosticEntityModel.id`. They are duplicated as
 * plain strings rather than shared because the data layer does not depend on `domain-core`; the
 * repository is the seam that translates the domain enum into these ids.
 */
private const val DIAGNOSTIC_UPTIME = "uptime"

/** Diagnostic entity id for the app version sensor. */
private const val DIAGNOSTIC_APP_VERSION = "app_version"

/** Diagnostic entity id for the LAN IP address sensor. */
private const val DIAGNOSTIC_IP_ADDRESS = "ip_address"

/** Diagnostic entity id for the RAM usage sensor. */
private const val DIAGNOSTIC_RAM_USAGE = "ram_usage"
