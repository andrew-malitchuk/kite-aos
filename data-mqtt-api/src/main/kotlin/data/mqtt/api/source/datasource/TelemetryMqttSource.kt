package data.mqtt.api.source.datasource

/**
 * Interface for managing MQTT telemetry data and device registration.
 *
 * This source handles communication with an MQTT broker, specifically for
 * reporting device status like motion detection and battery levels.
 * Implementations are responsible for broker connection lifecycle, Home Assistant
 * MQTT Discovery registration, and publishing telemetry state updates.
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
     * @param level The battery level percentage (0-100).
     */
    public suspend fun sendBatteryLevel(level: Int)
}
