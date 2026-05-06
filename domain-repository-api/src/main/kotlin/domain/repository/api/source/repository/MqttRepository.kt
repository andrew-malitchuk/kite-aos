package domain.repository.api.source.repository

import domain.core.source.model.MqttModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for MQTT telemetry operations.
 * This interface defines the contract for interacting with MQTT functionality
 * at the domain layer, abstracting the underlying data source implementation.
 *
 * @see MqttModel
 * @see data.repository.impl.source.repository.MqttRepositoryImpl
 * @since 0.0.1
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
     * @param friendlyName Human-readable name for the device used in Home Assistant discovery.
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
     * @param level The current battery level as a percentage (0–100).
     */
    public suspend fun sendBatteryLevel(level: Int)

    /**
     * Sends the current media volume level to the MQTT broker.
     *
     * @param level The volume level as a percentage (0–100).
     */
    public suspend fun sendVolume(level: Int)

    /**
     * Sends the current screen brightness level to the MQTT broker.
     *
     * @param level The brightness level (0–255, matching Android's brightness scale).
     */
    public suspend fun sendBrightness(level: Int)

    /**
     * Sends the current WebView URL to the MQTT broker.
     *
     * @param url The URL of the currently displayed page.
     */
    public suspend fun sendUrl(url: String)

    /**
     * Sends the current screen power state to the MQTT broker.
     *
     * @param isOn True if the screen is currently on (interactive), false otherwise.
     */
    public suspend fun sendScreenState(isOn: Boolean)

    /**
     * Returns a [Flow] that emits every inbound MQTT command as a [Pair] of (topic, payload).
     *
     * @return A [Flow] of (topic, payload) pairs for all subscribed command topics.
     */
    public fun observeCommands(): Flow<Pair<String, String>>

    /**
     * Observes changes to the MQTT configuration stored in preferences.
     *
     * @return A [Flow] emitting the current [MqttModel] on every configuration change, or null if not configured.
     * @since 0.0.1
     */
    public fun observeMqttConfiguration(): Flow<MqttModel?>

    /**
     * Retrieves the current MQTT configuration from preferences.
     *
     * @return The current [MqttModel], or null if not configured.
     * @since 0.0.1
     */
    public suspend fun getMqttConfiguration(): MqttModel?

    /**
     * Persists the provided MQTT [configuration] to preferences.
     *
     * @param configuration The MQTT configuration to save.
     * @since 0.0.1
     */
    public suspend fun setMqttConfiguration(configuration: MqttModel)
}
