package presentation.core.platform.source.motion

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.koin.core.annotation.Single
import presentation.core.platform.source.analyzer.MotionAnalyzer
import presentation.core.platform.source.streaming.JpegFrameEncoder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Default motion source for mobile: CameraX [ImageAnalysis] bound to the front camera.
 *
 * This is a near-verbatim extraction of the camera pipeline that previously lived inline in
 * `MotionService`. Frames are delivered as [CameraXMotionFrame], which analyses the raw
 * `ImageProxy` with the flat sampler and encodes via `ImageProxy.toBitmap()` — so mobile
 * motion detection and streaming behave exactly as before. Provides its own
 * [LifecycleRegistry] so CameraX binding is independent of the hosting service's lifecycle.
 *
 * @param context Application context.
 * @since 1.2.0
 */
@Single(binds = [MotionSource::class])
public class CameraXMotionSource(
    private val context: Context,
) : MotionSource, LifecycleOwner {

    // A LifecycleRegistry is one-shot: once moved to DESTROYED in stop() it cannot be RESUMED
    // again. This source is a Koin @Single reused across camera-source switches (start after a
    // prior stop), so the registry is recreated in start() rather than reused. See stop().
    private var lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry

    private var cameraExecutor: ExecutorService? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var config: MotionSourceConfig? = null
    private var onFrame: ((MotionFrame) -> Unit)? = null

    override val priority: Int = PRIORITY

    override val kind: CameraKind = CameraKind.BUILT_IN

    // Available on devices with any built-in camera (phones/tablets). Camera-less TV boxes report
    // false so the TV-specific sources are selected instead. Both facings are covered so the user
    // can pick the rear lens even on devices without a front camera.
    override fun isAvailable(context: Context): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    override fun start(config: MotionSourceConfig, onFrame: (MotionFrame) -> Unit) {
        this.config = config
        this.onFrame = onFrame
        cameraExecutor = Executors.newSingleThreadExecutor()
        // Fresh registry each start — a previous stop() may have left the old one DESTROYED,
        // which is a terminal state and would throw if moved back to RESUMED.
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            try {
                val provider = future.get()
                cameraProvider = provider
                bindCamera(provider)
            } catch (e: Exception) {
                Log.e(TAG, "Camera setup failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun updateConfig(config: MotionSourceConfig) {
        this.config = config
        val provider = cameraProvider ?: return
        ContextCompat.getMainExecutor(context).execute { bindCamera(provider) }
    }

    override fun stop() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        cameraExecutor?.shutdown()
        cameraExecutor = null
        onFrame = null
    }

    private fun bindCamera(provider: ProcessCameraProvider) {
        val cfg = config ?: return
        val executor = cameraExecutor ?: return
        val callback = onFrame ?: return
        val resolution = if (cfg.streamingEnabled) cfg.streamResolution else cfg.motionResolution

        val imageAnalysis =
            ImageAnalysis.Builder()
                .setTargetResolution(resolution)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(executor) { proxy -> callback(CameraXMotionFrame(proxy)) } }

        val selector = when (cfg.lens) {
            CameraLens.REAR -> CameraSelector.DEFAULT_BACK_CAMERA
            CameraLens.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
        }

        provider.unbindAll()
        // Fall back to whichever lens exists if the requested one is absent, so a device without a
        // front camera can still bind the rear one (and vice versa) rather than failing silently.
        val boundSelector = when {
            provider.hasCameraSafely(selector) -> selector
            provider.hasCameraSafely(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.DEFAULT_FRONT_CAMERA
            else -> CameraSelector.DEFAULT_BACK_CAMERA
        }
        provider.bindToLifecycle(this, boundSelector, imageAnalysis)
    }

    // ProcessCameraProvider.hasCamera throws CameraInfoUnavailableException rather than returning
    // false; wrap it so lens-availability probing is a simple boolean.
    private fun ProcessCameraProvider.hasCameraSafely(selector: CameraSelector): Boolean =
        runCatching { hasCamera(selector) }.getOrDefault(false)

    private companion object {
        private const val TAG = "CameraXMotionSource"
        private const val PRIORITY = 50
    }
}

/**
 * A CameraX [ImageProxy] wrapped as a [MotionFrame].
 *
 * Preserves the original mobile pipeline: motion analysis reads the Y-plane by absolute index
 * (not advancing the buffer) and MUST run before [encodeJpeg], because `toBitmap()` advances
 * the Y-plane position to its limit. `MotionService` always calls [analyze] before [encodeJpeg].
 *
 * @since 1.2.0
 */
internal class CameraXMotionFrame(private val proxy: ImageProxy) : MotionFrame {

    override fun analyze(analyzer: MotionAnalyzer, sensitivity: Float): Boolean =
        analyzer.analyze(proxy, sensitivity)

    override fun encodeJpeg(encoder: JpegFrameEncoder, rotationDegrees: Int, quality: Int): ByteArray? =
        encoder.encodeFromBitmap(proxy.toBitmap(), rotationDegrees, quality)

    override fun close() = proxy.close()
}
