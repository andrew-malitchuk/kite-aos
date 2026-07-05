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
     */
    public suspend fun connect(
        server: String,
        port: Int,
        clientId: String,
        username: String,
        password: String,
        friendlyName: String,
    )

    /**
     * Safely disconnects from the broker and cleans up internal resources.
     *
     * After calling this method, no further telemetry will be published until
     * [connect] is called again.
     */
    public suspend fun disconnect()

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
     * Returns a [Flow] that emits every inbound MQTT command as a [Pair] of (topic, payload).
     *
     * Only topics that the client is subscribed to (command topics for volume, brightness,
     * screen, and app launch) will be emitted. Collectors should filter by topic prefix to
     * route commands to the appropriate handler.
     *
     * The flow never completes; it emits until the source is disconnected and the scope is cancelled.
     */
    public fun observeCommands(): Flow<Pair<String, String>>
}
