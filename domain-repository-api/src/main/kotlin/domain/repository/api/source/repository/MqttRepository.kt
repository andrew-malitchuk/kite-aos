package domain.repository.api.source.repository

import domain.core.source.model.MqttModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for MQTT telemetry operations.
 * This interface defines the contract for interacting with MQTT functionality
 * at the domain layer, abstracting the underlying data source implementation.
 */
public interface MqttRepository {
    /**
     * Connects to the MQTT broker.
     *
     * @param server The MQTT broker server address (e.g., "tcp://broker.hivemq.com").
     * @param port The port of the MQTT broker (e.g., 1883).
     * @param clientId The client ID to use for the MQTT connection.
     * @param username The username for authentication.
     * @param password The password for authentication.
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
     * Safely disconnects from the MQTT broker and cleans up resources.
     */
    public suspend fun disconnect()


    /**
     * Sends the current motion detection state to the MQTT broker.
     *
     * @param isDetected True if motion is detected, false otherwise.
     */
    public suspend fun sendMotion(isDetected: Boolean)

    /**
     * Sends the current battery percentage to the MQTT broker.
     *
     * @param level The current battery level as a percentage (0-100).
     */
    public suspend fun sendBatteryLevel(level: Int)

    public fun observeMqttConfiguration(): Flow<MqttModel?>
    public suspend fun getMqttConfiguration(): MqttModel?
    public suspend fun setMqttConfiguration(configuration: MqttModel)
}
