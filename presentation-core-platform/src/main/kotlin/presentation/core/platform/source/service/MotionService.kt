package presentation.core.platform.source.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import domain.usecase.api.source.usecase.device.EmitMoveDetectorMotionUseCase
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendMotionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import presentation.core.platform.R
import presentation.core.platform.core.helper.DevicePowerManager
import presentation.core.platform.source.analyzer.MotionAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException

/**
 * Service responsible for camera-based motion detection and device power management.
 *
 * It uses CameraX for image analysis and a dedicated [MotionAnalyzer] to detect presence.
 * When motion is detected, it wakes the device and reports the event to MQTT.
 */
@Suppress("MagicNumber")
public class MotionService : LifecycleService() {
    // region Injected Dependencies
    private val getMoveDetectorUseCase: GetMoveDetectorUseCase by inject()
    private val emitMoveDetectorMotionUseCase: EmitMoveDetectorMotionUseCase by inject()
    private val mqttSendMotionUseCase: MqttSendMotionUseCase by inject()
    // endregion

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var cameraExecutor: ExecutorService

    // region Helpers
    private lateinit var motionAnalyzer: MotionAnalyzer
    private lateinit var devicePowerManager: DevicePowerManager
    // endregion

    // region State Management
    private var lastMotionTime = System.currentTimeMillis()
    private var wasMotionDetectedLast = false
    private var lastMqttSentTime = 0L

    // Feedback protection state (prevents light from screen waking the camera)
    private var isChangingState = false
    private var lastStateChangeTime = 0L
    // endregion

    // region Configuration
    private var isEnabled = true
    private var sensitivity = 5.0f
    private var dimDelaySeconds = 15
    private var lockDelaySeconds = 30
    // endregion

    public companion object {
        private const val TAG = "MotionService"
        private const val NOTIFICATION_CHANNEL_ID = "Motion Service"
        private const val NOTIFICATION_CHANNEL_NAME = "Motion Guard"
        private const val NOTIFICATION_ID = 1
        private const val MOTION_STOP_DEBOUNCE_SECONDS = 2L

        /** Blindness period to prevent light feedback loop (screen brightness triggering motion) */
        private const val STATE_CHANGE_COOLDOWN_MS = 2500L

        /** Power Optimization Constants */
        private const val MQTT_THROTTLE_MS = 2000L
        private const val IDLE_POLL_DELAY_MS = 10000L
        private const val ACTIVE_POLL_DELAY_MS = 5000L
        private val LOW_RES_SIZE = Size(176, 144)
    }

    /**
     * Initializes the service, creates the camera executor and power manager,
     * and starts the foreground notification.
     */
    override fun onCreate() {
        super.onCreate()
        cameraExecutor = Executors.newSingleThreadExecutor()
        motionAnalyzer = MotionAnalyzer()
        devicePowerManager = DevicePowerManager(this)

        setupNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    /**
     * Starts the service initialization logic and ensures it remains sticky.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initializeService()
        return START_STICKY
    }

    /**
     * Fetches configuration and sets up the camera and inactivity checker.
     */
    private fun initializeService() {
        lifecycleScope.launch {
            try {
                val model = getMoveDetectorUseCase().getOrNull()
                isEnabled = model?.enabled ?: true
                if (!isEnabled) {
                    stopSelf()
                    return@launch
                }

                sensitivity = (model?.sensitivity ?: 50).toFloat() / 10f
                dimDelaySeconds = model?.dimDelay?.toInt() ?: 15
                lockDelaySeconds = model?.screenTimeout?.toInt() ?: 30

                setupCamera()
                startInactivityChecker()
            } catch (e: CancellationException) {
                Log.i(TAG, "Initialization cancelled.")
            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed, using defaults.", e)
                setupCamera()
                startInactivityChecker()
            }
        }
    }

    /**
     * Periodically checks for inactivity to dim or lock the device.
     */
    private fun startInactivityChecker() {
        flow {
            while (true) {
                emit(Unit)
                val dynamicDelay =
                    if (wasMotionDetectedLast) ACTIVE_POLL_DELAY_MS else IDLE_POLL_DELAY_MS
                delay(dynamicDelay)
            }
        }
            .onEach { checkInactivity() }
            .launchIn(serviceScope)
    }

    /**
     * Logic to determine if the device should be dimmed or locked based on idle time.
     */
    private fun checkInactivity() {
        val idleTimeSeconds = (System.currentTimeMillis() - lastMotionTime) / 1000

        if (wasMotionDetectedLast && idleTimeSeconds >= MOTION_STOP_DEBOUNCE_SECONDS) {
            sendMotionMqttMessage(false)
            wasMotionDetectedLast = false
        }

        when {
            idleTimeSeconds >= lockDelaySeconds -> {
                enterBlindnessMode()
                devicePowerManager.lockDevice()
            }

            idleTimeSeconds >= dimDelaySeconds -> {
                if (devicePowerManager.setBrightness(DevicePowerManager.BRIGHTNESS_DIM)) {
                    enterBlindnessMode()
                }
            }

            else -> {
                if (devicePowerManager.setBrightness(DevicePowerManager.BRIGHTNESS_MAX)) {
                    enterBlindnessMode()
                }
            }
        }
    }

    /**
     * Configures and binds CameraX ImageAnalysis to the service lifecycle.
     */
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val previewUseCase = Preview.Builder().build()
                val imageAnalysis =
                    ImageAnalysis.Builder()
                        .setTargetResolution(LOW_RES_SIZE)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor) { imageProxy ->
                                processImageProxy(imageProxy)
                            }
                        }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    previewUseCase,
                    imageAnalysis,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Processes incoming [ImageProxy] frames, applying blindness cooldown and motion analysis.
     */
    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            val now = System.currentTimeMillis()

            if (isChangingState) {
                if (now - lastStateChangeTime > STATE_CHANGE_COOLDOWN_MS) {
                    isChangingState = false
                    motionAnalyzer.reset()
                    Log.d(TAG, "Blindness period ended. Resuming analysis.")
                } else {
                    return
                }
            }

            if (motionAnalyzer.analyze(imageProxy, sensitivity)) {
                onMotionDetected()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Analysis error", e)
        } finally {
            imageProxy.close()
        }
    }

    /**
     * Triggered when movement is identified. Wakes up the screen and notifies listeners.
     */
    private fun onMotionDetected() {
        Log.d(TAG, "Motion detected! Waking up.")
        lastMotionTime = System.currentTimeMillis()

        enterBlindnessMode()
        devicePowerManager.wakeUp()
        devicePowerManager.setBrightness(DevicePowerManager.BRIGHTNESS_MAX)

        lifecycleScope.launch {
            try {
                emitMoveDetectorMotionUseCase()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to emit motion event", e)
            }
        }

        if (!wasMotionDetectedLast) {
            sendMotionMqttMessage(true)
            wasMotionDetectedLast = true
        }
    }

    /**
     * Sends motion state to the MQTT broker with throttling.
     */
    private fun sendMotionMqttMessage(isDetected: Boolean) {
        val now = System.currentTimeMillis()
        if (now - lastMqttSentTime < MQTT_THROTTLE_MS) return

        lastMqttSentTime = now
        lifecycleScope.launch {
            mqttSendMotionUseCase(isDetected)
        }
    }

    /**
     * Activates "blindness" period where camera analysis is paused to ignore light changes.
     */
    private fun enterBlindnessMode() {
        isChangingState = true
        lastStateChangeTime = System.currentTimeMillis()
    }

    /**
     * Creates the mandatory notification channel for the foreground service.
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
     * Creates the notification shown while the service is running.
     */
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(presentation.core.localisation.R.string.motion_service_notification_title))
            .setContentText(getString(presentation.core.localisation.R.string.motion_service_notification_text))
            .setSmallIcon(R.drawable.ic_notification_icon_48)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * Cleans up resources, cancels coroutines, and shuts down the executor.
     */
    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        cameraExecutor.shutdown()
    }
}
