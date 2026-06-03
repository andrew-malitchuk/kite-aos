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
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import android.graphics.Bitmap
import android.graphics.Matrix
import domain.core.source.model.ScreenStateModel
import domain.usecase.api.source.usecase.device.EmitMoveDetectorMotionUseCase
import domain.usecase.api.source.usecase.device.EmitScreenStateUseCase
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.api.source.usecase.mqtt.MqttSendMotionUseCase
import domain.usecase.api.source.usecase.screensaver.GetScreensaverUseCase
import domain.usecase.api.source.usecase.streaming.ObserveStreamingConfigurationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import presentation.core.platform.R
import presentation.core.platform.core.helper.DevicePowerManager
import presentation.core.platform.source.analyzer.MotionAnalyzer
import presentation.core.platform.source.streaming.MjpegHttpServer
import java.io.ByteArrayOutputStream
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
    private val observeStreamingConfigurationUseCase: ObserveStreamingConfigurationUseCase by inject()
    private val mjpegHttpServer: MjpegHttpServer by inject()
    private val getScreensaverUseCase: GetScreensaverUseCase by inject()
    private val emitScreenStateUseCase: EmitScreenStateUseCase by inject()
    // endregion

    // SupervisorJob ensures a single coroutine failure does not cancel all service coroutines.
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    /** Dedicated single-thread executor for CameraX image analysis callbacks. */
    private lateinit var cameraExecutor: ExecutorService

    // region Streaming State
    private val frameFlow = MutableSharedFlow<ByteArray>(
        replay = 1,
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private var isStreamingEnabled = false
    private var streamingPort = DEFAULT_STREAMING_PORT
    private var streamingQuality = DEFAULT_STREAMING_QUALITY
    private var streamingFps = DEFAULT_STREAMING_FPS
    private var streamingRotation = DEFAULT_STREAMING_ROTATION
    private var lastStreamFrameTime = 0L
    private val jpegOutputStream = ByteArrayOutputStream(64 * 1024)
    private var cameraProvider: ProcessCameraProvider? = null
    // endregion

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

    /** Whether the screensaver overlay is enabled. */
    private var screensaverEnabled = false

    /** Seconds of inactivity after dim before the screensaver activates. */
    private var screensaverActivationDelay = 60L

    /** Tracks whether the screensaver is currently shown to avoid repeated emissions. */
    private var wasScreensaverActive = false
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

        /** Camera resolution used when MJPEG streaming is enabled. */
        private val STREAM_RES_SIZE = Size(640, 480)

        /** Default values for streaming configuration. */
        internal const val DEFAULT_STREAMING_PORT = 8080
        internal const val DEFAULT_STREAMING_QUALITY = 75
        internal const val DEFAULT_STREAMING_FPS = 10
        internal const val DEFAULT_STREAMING_ROTATION = 0

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

                if (isEnabled) {
                    // Sensitivity is stored as an integer 0–100 in settings; divide by 10 for the
                    // analyzer's expected 0.0–10.0 range.
                    sensitivity = (model?.sensitivity ?: 50).toFloat() / 10f
                    dimDelaySeconds = model?.dimDelay?.toInt() ?: 15
                    lockDelaySeconds = model?.screenTimeout?.toInt() ?: 30

                    val screensaverModel = getScreensaverUseCase().getOrNull()
                    screensaverEnabled = screensaverModel?.enabled == true
                    screensaverActivationDelay = screensaverModel?.activationDelay ?: 60L

                    startInactivityChecker()
                }

                // Camera and streaming must always initialize — streaming may run without motion.
                setupCamera()
                startStreamingObserver()
            } catch (e: CancellationException) {
                Log.i(TAG, "Initialization cancelled.")
            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed, using defaults.", e)
                setupCamera()
                startStreamingObserver()
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
            lockDelaySeconds > 0 && idleTimeSeconds >= lockDelaySeconds -> {
                enterBlindnessMode()
                devicePowerManager.lockDevice()
            }

            screensaverEnabled && idleTimeSeconds >= (dimDelaySeconds + screensaverActivationDelay) -> {
                if (devicePowerManager.setBrightness(DevicePowerManager.BRIGHTNESS_DIM)) {
                    enterBlindnessMode()
                }
                if (!wasScreensaverActive) {
                    wasScreensaverActive = true
                    lifecycleScope.launch {
                        emitScreenStateUseCase(ScreenStateModel.Screensaver)
                    }
                }
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
     * Observes streaming preference changes and manages the MJPEG server lifecycle accordingly.
     *
     * Uses [collectLatest] so that any in-flight streaming session is cancelled when the
     * configuration changes. When streaming is enabled the camera is rebound at a higher
     * resolution; when disabled it reverts to the low-res motion-only configuration.
     */
    private fun startStreamingObserver() {
        serviceScope.launch {
            @Suppress("OPT_IN_USAGE")
            observeStreamingConfigurationUseCase().debounce(300L).collectLatest { model ->
                val wasEnabled = isStreamingEnabled
                isStreamingEnabled = model?.enabled == true
                streamingPort = model?.port ?: DEFAULT_STREAMING_PORT
                streamingQuality = model?.quality ?: DEFAULT_STREAMING_QUALITY
                streamingFps = model?.fps ?: DEFAULT_STREAMING_FPS
                streamingRotation = model?.rotation ?: DEFAULT_STREAMING_ROTATION
                lastStreamFrameTime = 0L

                if (isStreamingEnabled) {
                    rebindCamera()
                    mjpegHttpServer.start(streamingPort, frameFlow)
                } else {
                    mjpegHttpServer.stop()
                    if (wasEnabled) rebindCamera()
                }
            }
        }
    }

    /** Rebinds CameraX with the appropriate resolution for the current streaming state. */
    private fun rebindCamera() {
        val provider = cameraProvider ?: return
        ContextCompat.getMainExecutor(this).execute { bindCamera(provider) }
    }

    /**
     * Obtains the [ProcessCameraProvider] and performs the initial camera bind.
     *
     * Resolution is [LOW_RES_SIZE] by default; once [startStreamingObserver] fires and
     * streaming is enabled, [rebindCamera] switches to [STREAM_RES_SIZE].
     *
     * @see bindCamera
     * @see processImageProxy
     * @since 0.0.1
     */
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                bindCamera(provider)
            } catch (e: Exception) {
                Log.e(TAG, "Camera setup failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Binds CameraX use cases to this service's lifecycle.
     *
     * When [isStreamingEnabled] is true, [STREAM_RES_SIZE] (640×480) is used so that JPEG
     * frames are usable for video; otherwise [LOW_RES_SIZE] (176×144) is used for efficiency.
     * Both cases share the same [processImageProxy] callback — streaming encoding is skipped
     * when [isStreamingEnabled] is false.
     *
     * Must be called on the main thread.
     *
     * @param provider The [ProcessCameraProvider] obtained during [setupCamera].
     * @since 0.1.0
     */
    private fun bindCamera(provider: ProcessCameraProvider) {
        val resolution = if (isStreamingEnabled) STREAM_RES_SIZE else LOW_RES_SIZE
        val imageAnalysis =
            ImageAnalysis.Builder()
                .setTargetResolution(resolution)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy -> processImageProxy(imageProxy) }
                }
        provider.unbindAll()
        provider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, imageAnalysis)
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
    @Suppress("MagicNumber")
    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            val now = System.currentTimeMillis()

            // Motion analysis runs first — MotionAnalyzer uses absolute-index buffer.get(i)
            // and does NOT advance the buffer position. Running it before toBitmap() is
            // required because toBitmap() advances the Y-plane position to its limit,
            // which would make buffer.remaining() == 0 and silently kill detection.
            //
            // Blindness mode applies ONLY to motion analysis — streaming must continue
            // uninterrupted so the MJPEG client does not freeze for 2.5 s after each
            // motion event.
            val isMotion: Boolean = when {
                isChangingState && now - lastStateChangeTime > STATE_CHANGE_COOLDOWN_MS -> {
                    isChangingState = false
                    motionAnalyzer.reset()
                    Log.d(TAG, "Blindness period ended. Resuming analysis.")
                    motionAnalyzer.analyze(imageProxy, sensitivity)
                }
                isChangingState -> false
                else -> motionAnalyzer.analyze(imageProxy, sensitivity)
            }

            // MJPEG streaming: runs unconditionally, independent of blindness state.
            if (isStreamingEnabled) {
                val minIntervalMs = 1000L / maxOf(1, streamingFps)
                if (now - lastStreamFrameTime >= minIntervalMs) {
                    lastStreamFrameTime = now
                    try {
                        val raw = imageProxy.toBitmap()
                        val frame = if (streamingRotation != 0) {
                            val matrix = Matrix().apply { postRotate(streamingRotation.toFloat()) }
                            val rotated = Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, false)
                            raw.recycle()
                            rotated
                        } else {
                            raw
                        }
                        jpegOutputStream.reset()
                        frame.compress(Bitmap.CompressFormat.JPEG, streamingQuality, jpegOutputStream)
                        frame.recycle()
                        frameFlow.tryEmit(jpegOutputStream.toByteArray())
                    } catch (e: Exception) {
                        Log.w(TAG, "JPEG encode failed", e)
                    }
                }
            }

            if (isMotion) {
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

        if (wasScreensaverActive) {
            wasScreensaverActive = false
            lifecycleScope.launch { emitScreenStateUseCase(ScreenStateModel.Active) }
        }

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
        runBlocking { mjpegHttpServer.stop() }
    }
}
