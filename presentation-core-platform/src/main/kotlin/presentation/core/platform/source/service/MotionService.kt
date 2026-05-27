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
 * Foreground service responsible for camera-based motion detection and device power management.
 *
 * This service uses CameraX [ImageAnalysis] at a low resolution ([LOW_RES_SIZE]) to continuously
 * analyze frames via [MotionAnalyzer]. When motion is detected, the device is woken up, screen
 * brightness is maximized, and the event is propagated to both the in-app state
 * ([EmitMoveDetectorMotionUseCase]) and the MQTT telemetry layer ([MqttSendMotionUseCase]).
 *
 * A "blindness" mechanism ([enterBlindnessMode]) temporarily pauses frame analysis after screen
 * brightness changes, preventing the camera from interpreting its own screen light as motion.
 *
 * Foreground service lifecycle:
 * - [onCreate]: Initializes the camera executor, helpers, notification channel, and starts the
 *   foreground notification.
 * - [onStartCommand]: Triggers async configuration fetch and camera setup; returns
 *   [START_STICKY] so the system restarts the service if it is killed.
 * - [onDestroy]: Cancels coroutines and shuts down the camera executor thread pool.
 *
 * @see MotionAnalyzer
 * @see DevicePowerManager
 * @see MqttService
 * @since 0.0.1
 */
// Suppress MagicNumber at class level — numeric constants used in this service are defined as
// named companion-object values (e.g., cooldown durations, poll intervals).
@Suppress("MagicNumber")
public class MotionService : LifecycleService() {
    // region Injected Dependencies
    private val getMoveDetectorUseCase: GetMoveDetectorUseCase by inject()
    private val emitMoveDetectorMotionUseCase: EmitMoveDetectorMotionUseCase by inject()
    private val mqttSendMotionUseCase: MqttSendMotionUseCase by inject()
    // endregion

    // SupervisorJob ensures a single coroutine failure does not cancel all service coroutines.
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    /** Dedicated single-thread executor for CameraX image analysis callbacks. */
    private lateinit var cameraExecutor: ExecutorService

    // region Helpers
    private lateinit var motionAnalyzer: MotionAnalyzer
    private lateinit var devicePowerManager: DevicePowerManager
    // endregion

    // region State Management
    /** Timestamp (epoch ms) of the last confirmed motion event. */
    private var lastMotionTime = System.currentTimeMillis()

    /** Tracks whether the previous analysis cycle detected motion (for edge detection). */
    private var wasMotionDetectedLast = false

    /** Timestamp (epoch ms) of the last MQTT motion message sent (for throttling). */
    private var lastMqttSentTime = 0L

    // Feedback protection state — prevents the screen's own light from triggering false motion.
    private var isChangingState = false
    private var lastStateChangeTime = 0L
    // endregion

    // region Configuration
    /** Whether the motion detector feature is enabled by user configuration. */
    private var isEnabled = true

    /** Motion sensitivity derived from user settings; passed directly to [MotionAnalyzer.analyze]. */
    private var sensitivity = 5.0f

    /** Seconds of inactivity before the screen is dimmed. */
    private var dimDelaySeconds = 15

    /** Seconds of inactivity before the device is locked. */
    private var lockDelaySeconds = 30
    // endregion

    /**
     * Companion constants for [MotionService].
     *
     * @since 0.0.1
     */
    public companion object {
        private const val TAG = "MotionService"
        private const val NOTIFICATION_CHANNEL_ID = "Motion Service"
        private const val NOTIFICATION_CHANNEL_NAME = "Motion Guard"

        /** Foreground notification ID. Must be unique across all foreground services in the app. */
        private const val NOTIFICATION_ID = 1

        /** Seconds to wait after the last motion frame before declaring motion has stopped. */
        private const val MOTION_STOP_DEBOUNCE_SECONDS = 2L

        /**
         * Blindness period (ms) to prevent the light-feedback loop where a brightness change
         * is picked up by the camera as motion.
         */
        private const val STATE_CHANGE_COOLDOWN_MS = 2500L

        /** Minimum interval (ms) between consecutive MQTT motion messages. */
        private const val MQTT_THROTTLE_MS = 2000L

        /** Polling delay (ms) when no motion is detected — longer interval saves battery. */
        private const val IDLE_POLL_DELAY_MS = 10000L

        /** Polling delay (ms) while motion is actively detected — shorter for responsiveness. */
        private const val ACTIVE_POLL_DELAY_MS = 5000L

        /**
         * Target camera resolution for CameraX ImageAnalysis.
         *
         * 176x144 (QCIF) is intentionally very low to minimize CPU and memory usage. Motion
         * detection only needs coarse luminance data, not fine detail.
         */
        private val LOW_RES_SIZE = Size(176, 144)
    }

    /**
     * Initializes the service, creates the camera executor and power manager,
     * and starts the foreground notification.
     *
     * This is the first lifecycle callback; no camera binding happens here — that is deferred
     * to [initializeService] which runs after configuration is loaded.
     *
     * @see onStartCommand
     * @since 0.0.1
     */
    override fun onCreate() {
        super.onCreate()
        cameraExecutor = Executors.newSingleThreadExecutor()
        motionAnalyzer = MotionAnalyzer()
        devicePowerManager = DevicePowerManager(this)

        setupNotificationChannel()
        // startForeground must be called within 5 seconds of the service being started,
        // otherwise the system throws a ForegroundServiceDidNotStartInTimeException.
        startForeground(NOTIFICATION_ID, createNotification())
    }

    /**
     * Handles the start command and ensures the service remains sticky.
     *
     * Delegates to [initializeService] for async configuration loading and camera setup.
     * Returning [START_STICKY] instructs the system to recreate the service after a low-memory
     * kill.
     *
     * @param intent The [Intent] supplied to [android.content.Context.startService].
     * @param flags Additional data about the start request.
     * @param startId A unique integer representing the start request.
     * @return [START_STICKY] to ensure the service is restarted if killed.
     * @see initializeService
     * @since 0.0.1
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initializeService()
        return START_STICKY
    }

    /**
     * Fetches the motion detector configuration from the domain layer and sets up the camera
     * pipeline and inactivity checker.
     *
     * If the motion detector is disabled in user settings, the service stops itself immediately.
     * On configuration fetch failure, the service falls back to default values.
     *
     * @see GetMoveDetectorUseCase
     * @since 0.0.1
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

                // Sensitivity is stored as an integer 0–100 in settings; divide by 10 for the
                // analyzer's expected 0.0–10.0 range.
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
     * Starts an infinite polling flow that periodically checks for device inactivity.
     *
     * The polling interval adapts dynamically: [ACTIVE_POLL_DELAY_MS] while motion is occurring
     * (for faster dim/lock response) and [IDLE_POLL_DELAY_MS] when idle (to save CPU).
     *
     * @see checkInactivity
     * @since 0.0.1
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
     * Evaluates the current idle duration and transitions the device screen accordingly.
     *
     * The state machine has three tiers:
     * 1. **Lock** — idle time >= [lockDelaySeconds]: lock the device.
     * 2. **Dim** — idle time >= [dimDelaySeconds]: reduce brightness to [DevicePowerManager.BRIGHTNESS_DIM].
     * 3. **Bright** — otherwise: restore brightness to [DevicePowerManager.BRIGHTNESS_MAX].
     *
     * After any brightness or lock change, [enterBlindnessMode] is called to prevent
     * false-positive motion from the screen light change.
     *
     * @see DevicePowerManager
     * @since 0.0.1
     */
    private fun checkInactivity() {
        val idleTimeSeconds = (System.currentTimeMillis() - lastMotionTime) / 1000

        // If motion was detected last cycle but the debounce window has elapsed, send a
        // "motion stopped" MQTT message.
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
     * Configures and binds the CameraX [ImageAnalysis] use case to the service lifecycle.
     *
     * The front-facing camera is used at [LOW_RES_SIZE] resolution with a
     * [ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST] backpressure strategy, so only the most recent
     * frame is analyzed and older frames are dropped. A [Preview] use case is also bound
     * because some devices require it for ImageAnalysis to function properly.
     *
     * @see processImageProxy
     * @see MotionAnalyzer
     * @since 0.0.1
     */
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Preview is bound even though we don't display it — some devices require a
                // preview surface to be bound alongside ImageAnalysis.
                val previewUseCase = Preview.Builder().build()
                val imageAnalysis =
                    ImageAnalysis.Builder()
                        .setTargetResolution(LOW_RES_SIZE)
                        // STRATEGY_KEEP_ONLY_LATEST drops older frames if the analyzer is busy,
                        // ensuring we always process the most recent frame.
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor) { imageProxy ->
                                processImageProxy(imageProxy)
                            }
                        }

                // Unbind all existing use cases before re-binding to avoid IllegalStateException.
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
     * Processes a single incoming [ImageProxy] frame, applying the blindness cooldown and
     * delegating to [MotionAnalyzer.analyze] for motion detection.
     *
     * During the blindness period (after a screen state change), frames are silently discarded
     * to avoid false positives. Once the cooldown expires, the analyzer is reset and analysis
     * resumes.
     *
     * @param imageProxy The CameraX [ImageProxy] frame to process. Always closed in the
     * `finally` block regardless of outcome.
     * @see MotionAnalyzer.analyze
     * @see enterBlindnessMode
     * @since 0.0.1
     */
    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            val now = System.currentTimeMillis()

            // During the blindness window, skip analysis to avoid light-feedback false positives.
            if (isChangingState) {
                if (now - lastStateChangeTime > STATE_CHANGE_COOLDOWN_MS) {
                    isChangingState = false
                    // Reset the analyzer baseline so the first frame after blindness establishes
                    // a new reference point.
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
            // ImageProxy must always be closed to release the underlying buffer back to CameraX.
            imageProxy.close()
        }
    }

    /**
     * Triggered when the [MotionAnalyzer] reports movement. Wakes the device, restores
     * maximum brightness, emits an in-app motion event, and sends an MQTT notification.
     *
     * @see DevicePowerManager.wakeUp
     * @see EmitMoveDetectorMotionUseCase
     * @since 0.0.1
     */
    private fun onMotionDetected() {
        Log.d(TAG, "Motion detected! Waking up.")
        lastMotionTime = System.currentTimeMillis()

        // Enter blindness mode before changing brightness to prevent the camera from
        // interpreting the brightness change as additional motion.
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

        // Only send the "motion started" MQTT message on the rising edge (first detection).
        if (!wasMotionDetectedLast) {
            sendMotionMqttMessage(true)
            wasMotionDetectedLast = true
        }
    }

    /**
     * Sends a motion state message to the MQTT broker, throttled to at most one message
     * every [MQTT_THROTTLE_MS] milliseconds.
     *
     * @param isDetected `true` if motion has just started, `false` if motion has stopped.
     * @see MqttSendMotionUseCase
     * @since 0.0.1
     */
    private fun sendMotionMqttMessage(isDetected: Boolean) {
        val now = System.currentTimeMillis()
        // Throttle to avoid flooding the MQTT broker with rapid on/off messages.
        if (now - lastMqttSentTime < MQTT_THROTTLE_MS) return

        lastMqttSentTime = now
        lifecycleScope.launch {
            mqttSendMotionUseCase(isDetected)
        }
    }

    /**
     * Activates the "blindness" period during which camera frame analysis is paused.
     *
     * This prevents a light-feedback loop: when the screen brightness changes, the camera
     * would otherwise interpret the illumination shift as motion, causing an infinite cycle
     * of wake-dim-wake events.
     *
     * @see STATE_CHANGE_COOLDOWN_MS
     * @see processImageProxy
     * @since 0.0.1
     */
    private fun enterBlindnessMode() {
        isChangingState = true
        lastStateChangeTime = System.currentTimeMillis()
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
     * Creates the persistent notification displayed while the motion detection service is running.
     *
     * @return A low-priority [Notification] with a title and description from localised resources.
     * @since 0.0.1
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
     * Cleans up all resources when the service is destroyed.
     *
     * Cancels the [serviceJob] (which cascades to all coroutines in [serviceScope]) and shuts
     * down the [cameraExecutor] thread pool. CameraX lifecycle unbinding happens automatically
     * because the service's lifecycle is ending.
     *
     * @since 0.0.1
     */
    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        cameraExecutor.shutdown()
    }
}
