package presentation.core.platform.source.motion

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.RequiresApi
import org.koin.core.annotation.Single
import presentation.core.platform.source.analyzer.MotionAnalyzer
import presentation.core.platform.source.streaming.JpegFrameEncoder

/**
 * Preferred external-webcam motion source: a USB/external camera driven through the Camera2 API.
 *
 * On Android TV boxes — and on phones/tablets with USB-C OTG on Android 9+ — a UVC webcam attached
 * to the USB host port is exposed by the platform as a standard Camera2 device with
 * `LENS_FACING_EXTERNAL` (API 28+). This source opens that camera, streams `YUV_420_888` frames
 * through an [ImageReader] on a background thread, and wraps each frame as a [Camera2MotionFrame] —
 * reusing the same [MotionAnalyzer] luma pipeline and [JpegFrameEncoder] as every other source.
 * No third-party dependency is required, so it is preferred over [UvcMotionSource] (priority 40 vs
 * 30) whenever the platform surfaces the webcam natively.
 *
 * @param context Application context.
 * @since 1.2.0
 */
@Single(binds = [MotionSource::class])
public class Camera2ExternalMotionSource(
    private val context: Context,
) : MotionSource {

    override val kind: CameraKind = CameraKind.EXTERNAL

    private val cameraManager: CameraManager? =
        context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var imageReader: ImageReader? = null
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var config: MotionSourceConfig? = null
    private var onFrame: ((MotionFrame) -> Unit)? = null

    override val priority: Int = PRIORITY

    override fun isAvailable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL)) return false
        return runCatching { findExternalCameraId() != null }.getOrDefault(false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun start(config: MotionSourceConfig, onFrame: (MotionFrame) -> Unit) {
        this.config = config
        this.onFrame = onFrame
        backgroundThread = HandlerThread("Camera2Motion").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
        openCamera()
    }

    override fun updateConfig(config: MotionSourceConfig) {
        this.config = config
        // Resolution may have changed (streaming toggled) — rebuild the reader + session.
        closeSession()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && cameraDevice != null) {
            createSession(cameraDevice!!)
        }
    }

    override fun stop() {
        closeSession()
        cameraDevice?.close()
        cameraDevice = null
        imageReader?.close()
        imageReader = null
        backgroundThread?.quitSafely()
        backgroundThread = null
        backgroundHandler = null
        onFrame = null
    }

    private fun findExternalCameraId(): String? {
        val manager = cameraManager ?: return null
        return manager.cameraIdList.firstOrNull { id ->
            manager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_EXTERNAL
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun openCamera() {
        val manager = cameraManager ?: return
        val cameraId = findExternalCameraId() ?: run {
            Log.w(TAG, "No external camera present at open time.")
            return
        }
        try {
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    cameraDevice = device
                    createSession(device)
                }

                override fun onDisconnected(device: CameraDevice) {
                    device.close()
                    cameraDevice = null
                }

                override fun onError(device: CameraDevice, error: Int) {
                    Log.e(TAG, "Camera2 open error: $error")
                    device.close()
                    cameraDevice = null
                }
            }, backgroundHandler)
        } catch (e: SecurityException) {
            Log.e(TAG, "Camera permission not granted", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open external camera", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun createSession(device: CameraDevice) {
        val cfg = config ?: return
        val size = if (cfg.streamingEnabled) cfg.streamResolution else cfg.motionResolution
        val reader = ImageReader.newInstance(size.width, size.height, ImageFormat.YUV_420_888, MAX_IMAGES)
            .also { it.setOnImageAvailableListener({ r -> onImageAvailable(r) }, backgroundHandler) }
        imageReader = reader

        try {
            val request = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                .apply { addTarget(reader.surface) }

            @Suppress("DEPRECATION")
            device.createCaptureSession(
                listOf(reader.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        runCatching {
                            session.setRepeatingRequest(request.build(), null, backgroundHandler)
                        }.onFailure { Log.e(TAG, "setRepeatingRequest failed", it) }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Capture session configuration failed")
                    }
                },
                backgroundHandler,
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create capture session", e)
        }
    }

    private fun onImageAvailable(reader: ImageReader) {
        val image = runCatching { reader.acquireLatestImage() }.getOrNull() ?: return
        val callback = onFrame
        if (callback == null) {
            image.close()
            return
        }
        // The service closes the frame (and thus the image) after handling.
        callback(Camera2MotionFrame(image))
    }

    private fun closeSession() {
        captureSession?.close()
        captureSession = null
        imageReader?.close()
        imageReader = null
    }

    private companion object {
        private const val TAG = "Camera2MotionSource"
        private const val PRIORITY = 40
        private const val MAX_IMAGES = 2
    }
}

/**
 * A Camera2 `YUV_420_888` [Image] wrapped as a [MotionFrame].
 *
 * Motion analysis reads the Y plane (`planes[0]`) with stride awareness — `YUV_420_888` planes
 * are often row-padded (`rowStride > width`), which the flat CameraX sampler would misread.
 * The image is closed by the service after handling.
 *
 * @since 1.2.0
 */
internal class Camera2MotionFrame(private val image: Image) : MotionFrame {

    override fun analyze(analyzer: MotionAnalyzer, sensitivity: Float): Boolean {
        val plane = image.planes[0]
        return analyzer.analyze(
            yPlane = plane.buffer,
            rowStride = plane.rowStride,
            pixelStride = plane.pixelStride,
            width = image.width,
            height = image.height,
            sensitivity = sensitivity,
        )
    }

    override fun encodeJpeg(encoder: JpegFrameEncoder, rotationDegrees: Int, quality: Int): ByteArray? =
        runCatching { encoder.encodeFromYuv420(image, rotationDegrees, quality) }.getOrNull()

    override fun close() {
        runCatching { image.close() }
    }
}
