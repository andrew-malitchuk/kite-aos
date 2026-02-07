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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import presentation.core.platform.R

/**
 * Foreground service that maintains the MQTT connection for the application.
 *
 * It reactively observes the MQTT configuration from the domain layer and
 * automatically connects or disconnects the client based on the current settings.
 */
public class MqttService : LifecycleService() {

    private val observeMqttConfigurationUseCase: ObserveMqttConfigurationUseCase by inject()
    private val mqttConnectUseCase: MqttConnectUseCase by inject()
    private val mqttDisconnectUseCase: MqttDisconnectUseCase by inject()

    public companion object {
        private const val TAG = "MqttService"
        private const val NOTIFICATION_CHANNEL_ID = "Mqtt Service"
        private const val NOTIFICATION_CHANNEL_NAME = "Mqtt Connection"
        private const val NOTIFICATION_ID = 2
    }

    /**
     * Initializes the service, sets up the notification channel, and starts observing
     * the MQTT configuration from the domain layer.
     */
    override fun onCreate() {
        super.onCreate()
        setupNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        observeConfiguration()
    }

    /**
     * Listens for changes in the MQTT configuration and manages the connection lifecycle
     * (connect/disconnect) accordingly.
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
     */
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    /**
     * Creates the notification shown while the MQTT service is running.
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
     * Ensures the service remains sticky.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    /**
     * Cleans up the MQTT connection when the service is destroyed.
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
