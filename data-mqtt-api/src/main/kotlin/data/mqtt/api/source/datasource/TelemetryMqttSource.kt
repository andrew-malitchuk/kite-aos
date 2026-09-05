package data.mqtt.api.source.datasource

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing MQTT telemetry data and device registration.
 *
 * This source handles communication with an MQTT broker, specifically for
 * reporting device status like motion detection and battery levels, as well as
 * receiving remote control commands from Home Assistant for volume, brightness,
 * screen state, and application launching.
 *
 * Implementations are responsible for broker connection lifecycle, Home Assistant
 * MQTT Discovery registration, publishing telemetry state updates, subscribing to
 * command topics, and exposing an observable stream of inbound commands.
 *
 * @see data.mqtt.impl.source.datasource.TelemetryMqttSourceImpl
 * @since 0.0.1
 */
public interface TelemetryMqttSource {
    /**
     * Connects to the MQTT broker using the provided credentials and configuration.
     *
     * Implementations should handle automatic reconnection and register any
     * necessary Home Assistant discovery topics upon successful connection.
     *
     * @param server The broker address (e.g., "192.168.1.100").
     * @param port The broker port (e.g., 1883).
     * @param clientId A unique identifier for this client, used as a prefix for MQTT topics.
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @param friendlyName A human-readable name for the device, used in Home Assistant discovery.
     * @param model The device form-factor reported to Home Assistant (`"tv"` or `"tablet"`).
     * @param diagnostics Ids of the optional diagnostic entities to register (`"uptime"`,
     *   `"app_version"`, `"ip_address"`, `"ram_usage"`). Ids absent from this set are actively
     *   unregistered so that opting one out removes it from Home Assistant instead of leaving it
     *   stranded at its last published value.
     */
    public suspend fun connect(
        server: String,
        port: Int,
        clientId: String,
        username: String,
        password: String,
        friendlyName: String,
        model: String,
        diagnostics: Set<String>,
    )

    /**
     * Safely disconnects from the broker and cleans up internal resources.
     *
     * Publishes a retained `offline` availability payload before sending DISCONNECT. A graceful
     * disconnect makes the broker discard the Last Will, so without this explicit publish Home
     * Assistant would keep showing the panel as available after it deliberately went away.
     *
     * After calling this method, no further telemetry will be published until
     * [connect] is called again.
     */
    public suspend fun disconnect()

    /**
     * Removes every Home Assistant discovery entity this client has registered.
     *
     * Publishes an empty retained payload to each entity's `config` topic, which is how MQTT
     * Discovery expresses deletion. Call this when the user turns MQTT off — a plain [disconnect]
     * leaves the retained config messages on the broker, so the panel's entities would linger in
     * Home Assistant permanently and have to be removed by hand.
     *
     * Must be called while still connected; it is a no-op once the client is gone.
     */
    public suspend fun purgeDiscovery()

    /**
     * Sends the current motion detection state to the broker.
     *
     * The payload published is `"ON"` when motion is detected and `"OFF"` otherwise,
     * matching the Home Assistant binary sensor convention.
     *
     * @param isDetected `true` if motion is currently detected, `false` otherwise.
     */
    public suspend fun sendMotion(isDetected: Boolean)

    /**
     * Sends the current battery percentage to the broker.
     *
     * The payload published is the integer level as a plain string (e.g., `"85"`).
     *
     * @param level The battery level percentage (0–100).
     */
    public suspend fun sendBatteryLevel(level: Int)

    /**
     * Sends the current media volume level to the broker.
     *
     * The payload is a normalized integer in the range `0–100`.
     *
     * @param level The volume level percentage (0–100).
     */
    public suspend fun sendVolume(level: Int)

    /**
     * Sends the current screen brightness level to the broker.
     *
     * The payload matches the Android [android.provider.Settings.System.SCREEN_BRIGHTNESS]
     * range (0–255).
     *
     * @param level The screen brightness level (0–255).
     */
    public suspend fun sendBrightness(level: Int)

    /**
     * Sends the current WebView URL to the broker.
     *
     * Published whenever the WebView finishes loading a new page.
     *
     * @param url The URL of the currently displayed page.
     */
    public suspend fun sendUrl(url: String)

    /**
     * Sends the current screen power state to the broker.
     *
     * The payload published is `"ON"` when the screen is on (interactive) and `"OFF"` otherwise.
     *
     * @param isOn `true` if the screen is currently on, `false` otherwise.
     */
    public suspend fun sendScreenState(isOn: Boolean)

    /**
     * Sends the watchdog health state to the broker.
     *
     * Published to `{clientId}_watchdog/state`. Expected payloads: `"ok"`, `"fail(N)"`, `"recovering"`.
     *
     * @param state The watchdog state string.
     */
    public suspend fun sendWatchdogState(state: String)

    /**
     * Sends the device network connectivity state to the broker.
     *
     * Published to `{clientId}_network/state`. Payload is `"online"` or `"offline"`.
     *
     * @param isOnline `true` when the network is available, `false` when lost.
     */
    public suspend fun sendNetworkState(isOnline: Boolean)

    /**
     * Sends the MJPEG camera stream URL to the broker.
     *
     * Published whenever the stream server starts or the device IP changes.
     * An empty string signals that the stream is currently unavailable.
     *
     * @param url The full MJPEG stream URL (e.g., `http://192.168.1.100:8080/stream.mjpg`),
     *   or an empty string when the stream is not active.
     */
    public suspend fun sendCameraUrl(url: String)

    /**
     * Sends whether the dashboard backend is currently reachable.
     *
     * Reported as its own Home Assistant entity rather than through device availability: the panel
     * itself is healthy during a backend outage, so marking the whole device unavailable would be
     * wrong and would hide the panel's own working controls.
     *
     * @param isReachable `true` when the dashboard backend is answering.
     */
    public suspend fun sendDashboardState(isReachable: Boolean)

    /**
     * Publishes the low-frequency companion telemetry values to the broker.
     *
     * Each value goes to its own state topic as a plain scalar. Values whose entity was not
     * requested in the [connect] `diagnostics` set are skipped, so a disabled entity costs no
     * traffic.
     *
     * @param uptimeSeconds Seconds since the device last booted.
     * @param appVersion Installed app `versionName`; blank values are skipped.
     * @param ipAddress Current LAN IPv4 address; blank values are skipped.
     * @param ramUsagePercent Device-wide memory usage percentage (0-100).
     */
    public suspend fun sendCompanionTelemetry(
        uptimeSeconds: Long,
        appVersion: String,
        ipAddress: String,
        ramUsagePercent: Int,
    )

    /**
     * Returns a [Flow] that emits every inbound MQTT command as a [Pair] of (topic, payload).
     *
     * Only topics that the client is subscribed to are emitted: the per-entity command topics
     * (volume, brightness, screen, app launch, FAB, screensaver, clear cache, motion) and the
     * shared remote-command topic `{clientId}/command/set`, which carries a JSON envelope
     * `{ "action": "...", "value": "..." }` rather than a bare value. Collectors should filter by
     * topic to route commands to the appropriate handler.
     *
     * The flow never completes; it emits until the source is disconnected and the scope is cancelled.
     */
    public fun observeCommands(): Flow<Pair<String, String>>
}
