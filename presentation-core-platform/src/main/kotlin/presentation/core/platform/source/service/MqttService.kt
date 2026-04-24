package presentation.core.platform.source.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import domain.usecase.api.source.usecase.mqtt.MqttConnectUseCase
import domain.usecase.api.source.usecase.mqtt.MqttDisconnectUseCase
import domain.usecase.api.source.usecase.mqtt.ObserveMqttConfigurationUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import presentation.core.platform.R

/**
 * Foreground service that maintains the MQTT connection for the application.
 *
 * This service reactively observes the MQTT configuration from the domain layer via
 * [ObserveMqttConfigurationUseCase] and automatically connects or disconnects the MQTT client
 * whenever the configuration changes. Using [collectLatest] ensures that if a new configuration
 * arrives while a connection attempt is in progress, the previous attempt is cancelled.
 *
 * Foreground service lifecycle:
 * - [onCreate]: Sets up the notification channel, starts the foreground notification, and begins
 *   observing the MQTT configuration.
 * - [onStartCommand]: Returns [START_STICKY] so the system restarts the service if killed.
 * - [onDestroy]: Disconnects the MQTT client to release broker resources.
 *
 * @see ObserveMqttConfigurationUseCase
 * @see MqttConnectUseCase
 * @see MqttDisconnectUseCase
 * @see MotionService
 * @since 0.0.1
 */
public class MqttService : LifecycleService() {
    private val observeMqttConfigurationUseCase: ObserveMqttConfigurationUseCase by inject()
    private val mqttConnectUseCase: MqttConnectUseCase by inject()
    private val mqttDisconnectUseCase: MqttDisconnectUseCase by inject()

    /**
     * Companion constants for [MqttService].
     *
     * @since 0.0.1
     */
    public companion object {
        private const val TAG = "MqttService"
        private const val NOTIFICATION_CHANNEL_ID = "Mqtt Service"
        private const val NOTIFICATION_CHANNEL_NAME = "Mqtt Connection"

        /**
         * Foreground notification ID. Must be unique across all foreground services in the app
         * (distinct from [MotionService.NOTIFICATION_ID]).
         */
        private const val NOTIFICATION_ID = 2
    }

    /**
     * Initializes the service, sets up the notification channel, starts the foreground
     * notification, and begins observing the MQTT configuration from the domain layer.
     *
     * [startForeground] must be called within 5 seconds of the service being started,
     * otherwise the system throws a `ForegroundServiceDidNotStartInTimeException`.
     *
     * @see observeConfiguration
     * @since 0.0.1
     */
    override fun onCreate() {
        super.onCreate()
        setupNotificationChannel()
        // Start foreground immediately to satisfy the Android foreground service timing contract.
        startForeground(NOTIFICATION_ID, createNotification())
        observeConfiguration()
    }

    /**
     * Listens for changes in the MQTT configuration and manages the connection lifecycle
     * (connect/disconnect) accordingly.
     *
     * [collectLatest] is used so that if the configuration changes rapidly, only the latest
     * emission is fully processed — earlier connection/disconnection attempts are cancelled.
     *
     * @see ObserveMqttConfigurationUseCase
     * @see MqttConnectUseCase
     * @see MqttDisconnectUseCase
     * @since 0.0.1
     */
    private fun observeConfiguration() {
        lifecycleScope.launch {
            observeMqttConfigurationUseCase().collectLatest { config ->
                if (config?.enabled == true) {
                    Log.d(TAG, "MQTT enabled, attempting to connect...")
                    mqttConnectUseCase().onSuccess {
                        Log.i(TAG, "MQTT connected successfully.")
                    }.onFailure { e ->
                        Log.e(TAG, "Failed to connect to MQTT broker", e)
                    }
                } else {
                    Log.d(TAG, "MQTT disabled or configuration missing, disconnecting...")
                    mqttDisconnectUseCase().onFailure { e ->
                        Log.e(TAG, "Failed to disconnect MQTT client", e)
                    }
                }
            }
        }
    }

    /**
     * Creates the mandatory notification channel for the foreground service.
     *
     * Notification channels are required on Android 8.0 (API 26, Oreo) and above. The channel
     * is created with [NotificationManager.IMPORTANCE_LOW] to minimize user disruption.
     *
     * @since 0.0.1
     */
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW,
                )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    /**
     * Creates the persistent notification displayed while the MQTT service is running.
     *
     * @return A low-priority [Notification] with a title and description from localised resources.
     * @since 0.0.1
     */
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(presentation.core.localisation.R.string.mqtt_service_notification_title))
            .setContentText(getString(presentation.core.localisation.R.string.mqtt_service_notification_text))
            .setSmallIcon(R.drawable.ic_notification_icon_48)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * Handles the start command and ensures the service remains sticky.
     *
     * Returning [START_STICKY] instructs the system to recreate the service after a low-memory
     * kill, ensuring the MQTT connection is re-established.
     *
     * @param intent The [Intent] supplied to [android.content.Context.startService].
     * @param flags Additional data about the start request.
     * @param startId A unique integer representing the start request.
     * @return [START_STICKY] to ensure the service is restarted if killed.
     * @since 0.0.1
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    /**
     * Cleans up the MQTT connection when the service is destroyed.
     *
     * A disconnect attempt is launched in [lifecycleScope]. Note that because the lifecycle is
     * ending, this coroutine may be cancelled before completion if the process is being killed.
     * The MQTT broker will eventually clean up the session via its keep-alive timeout.
     *
     * @since 0.0.1
     */
    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            mqttDisconnectUseCase().onFailure { e ->
                Log.e(TAG, "Failed to disconnect MQTT client during service destruction", e)
            }
        }
        Log.i(TAG, "MqttService destroyed")
    }
}
