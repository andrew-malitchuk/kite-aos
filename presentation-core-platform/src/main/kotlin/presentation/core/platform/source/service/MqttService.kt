package presentation.core.platform.source.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import domain.usecase.api.source.usecase.mqtt.MqttConnectUseCase
import domain.usecase.api.source.usecase.mqtt.MqttDisconnectUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendBrightnessUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendCameraUrlUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendScreenStateUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendVolumeUseCase
import domain.usecase.api.source.usecase.mqtt.ObserveMqttCommandsUseCase
import domain.usecase.api.source.usecase.mqtt.ObserveMqttConfigurationUseCase
import domain.usecase.api.source.usecase.streaming.ObserveStreamingConfigurationUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import presentation.core.platform.R
import presentation.core.platform.core.helper.DevicePowerManager
import presentation.core.platform.core.helper.NetworkAddressResolver

/**
 * Foreground service that maintains the MQTT connection and handles bidirectional control.
 *
 * Responsibilities:
 * - Observes the MQTT configuration and connects/disconnects the broker reactively.
 * - On each successful connection, publishes initial device states (volume, brightness, screen)
 *   and observes inbound commands from Home Assistant via [ObserveMqttCommandsUseCase].
 * - Routes inbound commands to the appropriate Android system APIs:
 *   - `volume/set` → [AudioManager]
 *   - `brightness/set` → [DevicePowerManager]
 *   - `screen/set` → [DevicePowerManager] (wake/lock)
 *   - `app/launch` → [startActivity]
 * - Publishes screen state changes via a [BroadcastReceiver] for [Intent.ACTION_SCREEN_ON] /
 *   [Intent.ACTION_SCREEN_OFF].
 * - Publishes brightness changes via a [ContentObserver] on [Settings.System.SCREEN_BRIGHTNESS].
 *
 * @see ObserveMqttConfigurationUseCase
 * @see MqttConnectUseCase
 * @see MqttDisconnectUseCase
 * @see ObserveMqttCommandsUseCase
 * @see MotionService
 * @since 0.0.1
 */
// Suppressed: brightness constants (10, 255) and volume normalization factor (100) are named in
// DevicePowerManager companion or are self-evident from context.
@Suppress("MagicNumber")
public class MqttService : LifecycleService() {
    private val observeMqttConfigurationUseCase: ObserveMqttConfigurationUseCase by inject()
    private val mqttConnectUseCase: MqttConnectUseCase by inject()
    private val mqttDisconnectUseCase: MqttDisconnectUseCase by inject()
    private val observeMqttCommandsUseCase: ObserveMqttCommandsUseCase by inject()
    private val mqttSendVolumeUseCase: MqttSendVolumeUseCase by inject()
    private val mqttSendBrightnessUseCase: MqttSendBrightnessUseCase by inject()
    private val mqttSendScreenStateUseCase: MqttSendScreenStateUseCase by inject()
    private val mqttSendCameraUrlUseCase: MqttSendCameraUrlUseCase by inject()
    private val observeStreamingConfigurationUseCase: ObserveStreamingConfigurationUseCase by inject()

    private lateinit var audioManager: AudioManager
    private lateinit var powerManager: PowerManager
    private lateinit var devicePowerManager: DevicePowerManager

    /**
     * BroadcastReceiver that listens for [Intent.ACTION_SCREEN_ON] and [Intent.ACTION_SCREEN_OFF]
     * system broadcasts to publish screen state changes to the MQTT broker.
     */
    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isOn = when (intent.action) {
                Intent.ACTION_SCREEN_ON -> true
                Intent.ACTION_SCREEN_OFF -> false
                else -> return
            }
            lifecycleScope.launch {
                mqttSendScreenStateUseCase(isOn)
            }
        }
    }

    /**
     * ContentObserver that detects [Settings.System.SCREEN_BRIGHTNESS] changes and publishes
     * the updated value to the MQTT broker.
     */
    private val brightnessObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            val brightness = Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                DevicePowerManager.BRIGHTNESS_MAX,
            )
            lifecycleScope.launch {
                mqttSendBrightnessUseCase(brightness)
            }
        }
    }

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
     * Initializes the service, sets up system managers, registers receivers/observers,
     * starts the foreground notification, and begins observing MQTT configuration.
     *
     * @since 0.0.1
     */
    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        devicePowerManager = DevicePowerManager(this)

        val screenFilter = IntentFilter(Intent.ACTION_SCREEN_ON).apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, screenFilter)

        contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            false,
            brightnessObserver,
        )

        setupNotificationChannel()
        // Start foreground immediately to satisfy the Android foreground service timing contract.
        startForeground(NOTIFICATION_ID, createNotification())
        observeConfiguration()
    }

    /**
     * Listens for MQTT configuration changes and manages the connection lifecycle.
     *
     * On a successful connection, publishes the initial device states and starts observing
     * inbound commands. [collectLatest] cancels the previous block on each new config emission,
     * which also cancels the command observation coroutine automatically.
     *
     * @since 0.0.1
     */
    private fun observeConfiguration() {
        lifecycleScope.launch {
            observeMqttConfigurationUseCase().collectLatest { config ->
                if (config?.enabled == true) {
                    Log.d(TAG, "MQTT enabled, attempting to connect...")
                    val result = mqttConnectUseCase()
                    if (result.isSuccess) {
                        Log.i(TAG, "MQTT connected successfully.")
                        publishInitialStates()
                        launch { observeStreamingUrl() }
                        observeAndRouteCommands(clientId = config.clientId ?: "")
                    } else {
                        Log.e(TAG, "Failed to connect to MQTT broker", result.exceptionOrNull())
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
     * Publishes the current state of all device entities to the MQTT broker immediately
     * after a fresh connection. This ensures Home Assistant shows accurate initial values.
     */
    private suspend fun publishInitialStates() {
        try {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val normalizedVolume = if (maxVolume > 0) currentVolume * 100 / maxVolume else 0
            mqttSendVolumeUseCase(normalizedVolume)

            val brightness = Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                DevicePowerManager.BRIGHTNESS_MAX,
            )
            mqttSendBrightnessUseCase(brightness)

            mqttSendScreenStateUseCase(powerManager.isInteractive)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish initial states", e)
        }
    }

    /**
     * Observes streaming configuration changes and publishes the MJPEG stream URL to HA.
     *
     * Publishes a full URL when streaming is enabled, or an empty string when disabled.
     * Runs as a peer coroutine inside the MQTT-connected block so it is cancelled automatically
     * when the MQTT config changes or the connection is lost.
     */
    private suspend fun observeStreamingUrl() {
        observeStreamingConfigurationUseCase().collect { model ->
            val url = if (model?.enabled == true) {
                val ip = NetworkAddressResolver.getLocalIpAddress() ?: ""
                val port = model.port ?: MotionService.DEFAULT_STREAMING_PORT
                if (ip.isNotEmpty()) "http://$ip:$port/stream.mjpg" else ""
            } else {
                ""
            }
            mqttSendCameraUrlUseCase(url)
        }
    }

    /**
     * Collects inbound MQTT commands and routes each to the appropriate handler.
     *
     * This is a suspend function that blocks until cancelled (e.g., when a new config
     * arrives and [collectLatest] cancels the previous block).
     *
     * @param clientId The current client identifier used to match command topic prefixes.
     */
    private suspend fun observeAndRouteCommands(clientId: String) {
        observeMqttCommandsUseCase().collect { (topic, payload) ->
            routeCommand(clientId = clientId, topic = topic, payload = payload)
        }
    }

    /**
     * Routes a single inbound (topic, payload) pair to the appropriate handler.
     *
     * Topic convention: `{clientId}_{entity}/{entity}/set` or `{clientId}_app/app/launch`.
     *
     * @param clientId The current client identifier used to build topic prefixes.
     * @param topic The full MQTT topic of the inbound message.
     * @param payload The raw string payload of the inbound message.
     */
    private suspend fun routeCommand(clientId: String, topic: String, payload: String) {
        when (topic) {
            "${clientId}_volume/volume/set" -> handleVolumeCommand(payload)
            "${clientId}_brightness/brightness/set" -> handleBrightnessCommand(payload)
            "${clientId}_screen/screen/set" -> handleScreenCommand(payload)
            "${clientId}_app/app/launch" -> handleAppLaunchCommand(payload)
        }
    }

    /**
     * Applies a volume command to the device's media stream.
     *
     * @param payload Normalized volume level as a string (0–100).
     */
    private suspend fun handleVolumeCommand(payload: String) {
        val normalizedLevel = payload.trim().toIntOrNull() ?: return
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVolume = normalizedLevel * maxVolume / 100
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
        // Publish confirmed state after the change.
        val confirmedNormalized = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVolume
        mqttSendVolumeUseCase(confirmedNormalized)
    }

    /**
     * Applies a brightness command to the screen.
     *
     * @param payload Brightness level as a string (0–255).
     */
    private suspend fun handleBrightnessCommand(payload: String) {
        val level = payload.trim().toIntOrNull() ?: return
        val clamped = level.coerceIn(0, DevicePowerManager.BRIGHTNESS_MAX)
        devicePowerManager.setBrightness(clamped)
        // Brightness observer will publish the confirmed state automatically.
    }

    /**
     * Applies a screen on/off command to the device.
     *
     * `"ON"` → wakes the device; `"OFF"` → locks the device.
     *
     * @param payload `"ON"` or `"OFF"`.
     */
    private fun handleScreenCommand(payload: String) {
        when (payload.trim().uppercase()) {
            "ON" -> {
                devicePowerManager.wakeUp()
                lifecycleScope.launch { mqttSendScreenStateUseCase(true) }
            }
            "OFF" -> {
                devicePowerManager.lockDevice()
                lifecycleScope.launch { mqttSendScreenStateUseCase(false) }
            }
        }
    }

    /**
     * Launches an installed application by its package name.
     *
     * The activity is started with [Intent.FLAG_ACTIVITY_NEW_TASK] since this is called
     * from a service context.
     *
     * @param payload The package name of the application to launch (e.g., `"com.example.app"`).
     */
    private fun handleAppLaunchCommand(payload: String) {
        val packageName = payload.trim()
        if (packageName.isEmpty()) return
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
            Log.i(TAG, "Launched app: $packageName")
        } else {
            Log.w(TAG, "App not found for package: $packageName")
        }
    }

    /**
     * Creates the mandatory notification channel for the foreground service.
     *
     * @since 0.0.1
     */
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
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
     * @since 0.0.1
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    /**
     * Cleans up resources: unregisters receivers/observers and disconnects MQTT.
     *
     * @since 0.0.1
     */
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(screenStateReceiver)
        } catch (_: Exception) { }
        try {
            contentResolver.unregisterContentObserver(brightnessObserver)
        } catch (_: Exception) { }
        lifecycleScope.launch {
            mqttDisconnectUseCase().onFailure { e ->
                Log.e(TAG, "Failed to disconnect MQTT client during service destruction", e)
            }
        }
        Log.i(TAG, "MqttService destroyed")
    }
}
