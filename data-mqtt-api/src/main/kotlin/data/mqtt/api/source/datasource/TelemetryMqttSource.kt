package data.mqtt.api.source.datasource

/**
 * Interface for managing MQTT telemetry data and device registration.
 *
 * This source handles communication with an MQTT broker, specifically for
 * reporting device status like motion detection and battery levels.
 */
public interface TelemetryMqttSource {
    /**
     * Connects to the MQTT broker using the provided credentials and configuration.
     *
     * @param server The broker address (e.g., "192.168.1.100").
     * @param port The broker port (e.g., 1883).
     * @param clientId A unique identifier for this client.
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @param friendlyName A human-readable name for the device, used in discovery.
     */
    public suspend fun connect(
        server: String,
        port: Int,
        clientId: String,
        username: String,
        password: String,
        friendlyName: String
    )

    /**
     * Safely disconnects from the broker and cleans up internal resources.
     */
    public suspend fun disconnect()

    /**
     * Sends the current motion detection state to the broker.
     *
     * @param isDetected `true` if motion is currently detected, `false` otherwise.
     */
    public suspend fun sendMotion(isDetected: Boolean)

    /**
     * Sends the current battery percentage to the broker.
     *
     * @param level The battery level percentage (0-100).
     */
    public suspend fun sendBatteryLevel(level: Int)
}
