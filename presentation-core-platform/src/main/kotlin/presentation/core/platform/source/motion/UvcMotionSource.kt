package presentation.core.platform.source.motion

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.callback.IDeviceConnectCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.serenegiant.usb.USBMonitor
import org.koin.core.annotation.Single
import presentation.core.platform.source.analyzer.MotionAnalyzer
import presentation.core.platform.source.streaming.JpegFrameEncoder

/**
 * Fallback TV motion source: a USB (UVC) webcam driven through the AndroidUSBCamera engine.
 *
 * Used on TV boxes whose platform does **not** surface an attached USB webcam as a Camera2
 * `LENS_FACING_EXTERNAL` device (so [Camera2ExternalMotionSource] reports unavailable) but which
 * still expose it as a raw UVC device on the USB host bus. The AndroidUSBCamera (`libausbc`)
 * engine claims the device over USB and delivers NV21 preview frames, which we wrap as
 * [UvcMotionFrame] — reusing the exact same [MotionAnalyzer] luma pipeline and [JpegFrameEncoder]
 * as every other source, so motion detection and MJPEG streaming behave identically.
 *
 * Priority 30 sits below Camera2-external (40): the platform-native Camera2 path is always
 * preferred when the OS surfaces the webcam itself, and this AUSBC path is the fallback for
 * devices (some TV boxes, some phones) that expose the webcam only as a raw UVC device.
 *
 * @param context Application context used for USB enumeration and to drive the camera client.
 * @since 1.2.0
 */
@Single(binds = [MotionSource::class])
public class UvcMotionSource(
    private val context: Context,
) : MotionSource {

    private var client: MultiCameraClient? = null
    private var camera: MultiCameraClient.Camera? = null
    private var glSurface: HeadlessGlSurfaceTexture? = null
    private var config: MotionSourceConfig? = null
    private var onFrame: ((MotionFrame) -> Unit)? = null

    // libausbc delivers preview data without dimensions; cache the size we opened with so the
    // NV21 buffer can be interpreted. Written when a camera opens, read on the preview thread.
    @Volatile private var frameWidth = 0
    @Volatile private var frameHeight = 0

    override val priority: Int = PRIORITY

    override val kind: CameraKind = CameraKind.EXTERNAL

    override fun isAvailable(context: Context): Boolean {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as? UsbManager ?: return false
        return runCatching {
            usbManager.deviceList.values.any(::isUvcDevice)
        }.getOrDefault(false)
    }

    override fun start(config: MotionSourceConfig, onFrame: (MotionFrame) -> Unit) {
        this.config = config
        this.onFrame = onFrame

        val newClient = MultiCameraClient(context, deviceConnectCallback)
        client = newClient
        newClient.register()
    }

    override fun updateConfig(config: MotionSourceConfig) {
        this.config = config
        // Resolution may have changed (streaming toggled). Reopen the active camera with the
        // new request; if none is open yet, the next onConnectDev picks up the stored config.
        val device = camera?.getUsbDevice() ?: return
        val ctrlBlock = pendingCtrlBlock ?: return
        camera?.closeCamera()
        openCamera(device, ctrlBlock)
    }

    override fun stop() {
        camera?.closeCamera()
        camera = null
        glSurface?.release()
        glSurface = null
        pendingCtrlBlock = null
        client?.unRegister()
        client?.destroy()
        client = null
        onFrame = null
    }

    // Retained so updateConfig can reopen the same device without waiting for a re-attach.
    private var pendingCtrlBlock: USBMonitor.UsbControlBlock? = null

    private val deviceConnectCallback = object : IDeviceConnectCallBack {
        override fun onAttachDev(device: UsbDevice?) {
            // Only claim genuine UVC cameras; ignore unrelated USB peripherals (keyboards, hubs).
            if (device != null && isUvcDevice(device)) {
                client?.requestPermission(device)
            }
        }

        // NB: `onDetachDec` / `onDisConnectDec` are (mis)spelled that way in libausbc's
        // interface — the override names must match the library, typo and all.
        override fun onDetachDec(device: UsbDevice?) {
            camera?.closeCamera()
            camera = null
            glSurface?.release()
            glSurface = null
            pendingCtrlBlock = null
        }

        override fun onConnectDev(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
            if (device == null || ctrlBlock == null) return
            pendingCtrlBlock = ctrlBlock
            openCamera(device, ctrlBlock)
        }

        override fun onDisConnectDec(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
            camera?.closeCamera()
            camera = null
            glSurface?.release()
            glSurface = null
        }

        override fun onCancelDev(device: UsbDevice?) {
            // Permission denied by the user — nothing to open. The factory will have already
            // fallen through to the next source at selection time if this never succeeds.
        }
    }

    private val previewDataCallback = object : IPreviewDataCallBack {
        override fun onPreviewData(data: ByteArray?, format: IPreviewDataCallBack.DataFormat) {
            val callback = onFrame ?: return
            val bytes = data ?: return
            // libausbc only emits NV21 in 3.2.7; guard so a future format never misfeeds the analyzer.
            if (format != IPreviewDataCallBack.DataFormat.NV21) return
            val width = frameWidth
            val height = frameHeight
            if (width <= 0 || height <= 0) return
            // Consumed synchronously by MotionService (analyze + optional JPEG encode) before the
            // next callback, so libausbc reusing this buffer is safe — no defensive copy needed.
            callback(UvcMotionFrame(bytes, width, height))
        }
    }

    private fun openCamera(device: UsbDevice, ctrlBlock: USBMonitor.UsbControlBlock) {
        val cfg = config ?: return
        val size = if (cfg.streamingEnabled) cfg.streamResolution else cfg.motionResolution

        // libausbc requires a real render target and crashes (uncatchably, on its own camera
        // thread) on a null one — so hand it an off-screen SurfaceTexture we never display. Reopen
        // may be called with a new resolution, so replace any prior surface.
        glSurface?.release()
        val surface = HeadlessGlSurfaceTexture(size.width, size.height)
        glSurface = surface

        val newCamera = MultiCameraClient.Camera(context, device).apply {
            setUsbControlBlock(ctrlBlock)
            addPreviewDataCallBack(previewDataCallback)
        }
        camera = newCamera

        val request = CameraRequest.Builder()
            .setPreviewWidth(size.width)
            .setPreviewHeight(size.height)
            .create()

        // The off-screen texture keeps the engine's preview loop alive; motion frames still arrive
        // through the NV21 data callback, and the texture's pixels are drained and discarded.
        runCatching { newCamera.openCamera(surface.surfaceTexture, request) }
            .onFailure { Log.e(TAG, "Failed to open UVC camera", it) }

        // The engine may snap to the nearest supported size; trust the request otherwise.
        val actual = newCamera.getPreviewSize()
        frameWidth = actual?.width ?: size.width
        frameHeight = actual?.height ?: size.height
    }

    // A device is a UVC camera if any interface is the USB Video class, or a Miscellaneous
    // (IAD) device whose composite typically fronts a video function.
    private fun isUvcDevice(device: UsbDevice): Boolean =
        (0 until device.interfaceCount).any { index ->
            when (device.getInterface(index).interfaceClass) {
                UsbConstants.USB_CLASS_VIDEO, UsbConstants.USB_CLASS_MISC -> true
                else -> false
            }
        }

    private companion object {
        private const val TAG = "UvcMotionSource"
        private const val PRIORITY = 30
    }
}

/**
 * A UVC webcam NV21 frame wrapped as a [MotionFrame].
 *
 * NV21 is a contiguous buffer (Y plane, then interleaved VU), so the Y plane is the first
 * `width * height` bytes with `rowStride == width` and `pixelStride == 1` — read directly by the
 * stride-aware [MotionAnalyzer] overload and encoded by [JpegFrameEncoder.encodeFromNv21]. The
 * backing array is owned by the engine and reused after the synchronous callback returns, so
 * [close] has nothing to release.
 *
 * @since 1.2.0
 */
internal class UvcMotionFrame(
    private val nv21: ByteArray,
    private val width: Int,
    private val height: Int,
) : MotionFrame {

    override fun analyze(analyzer: MotionAnalyzer, sensitivity: Float): Boolean =
        analyzer.analyze(
            yPlane = java.nio.ByteBuffer.wrap(nv21),
            rowStride = width,
            pixelStride = 1,
            width = width,
            height = height,
            sensitivity = sensitivity,
        )

    override fun encodeJpeg(encoder: JpegFrameEncoder, rotationDegrees: Int, quality: Int): ByteArray? =
        runCatching { encoder.encodeFromNv21(nv21, width, height, rotationDegrees, quality) }.getOrNull()

    override fun close() {
        // Engine-owned buffer; nothing to release.
    }
}
