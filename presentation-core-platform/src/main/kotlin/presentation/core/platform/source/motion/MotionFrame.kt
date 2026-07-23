package presentation.core.platform.source.motion

import presentation.core.platform.source.analyzer.MotionAnalyzer
import presentation.core.platform.source.streaming.JpegFrameEncoder

/**
 * A single camera frame, abstracted over its origin (CameraX `ImageProxy`, Camera2
 * `YUV_420_888`, or a UVC NV21 buffer).
 *
 * Each [MotionSource] wraps its native frame in a [MotionFrame] and hands it to the service,
 * which owns the stateful blindness/streaming logic. Keeping analysis and encoding as frame
 * methods (rather than exposing raw pixels) lets the mobile CameraX path stay byte-identical
 * — it still analyzes the `ImageProxy` with the flat sampler and encodes via `toBitmap()` —
 * while TV frames use the stride-aware paths, all through one service-side handler.
 *
 * @since 1.2.0
 */
public interface MotionFrame {

    /**
     * Runs motion detection on this frame using the shared [analyzer].
     *
     * Implementations call the [MotionAnalyzer] overload appropriate to their pixel layout
     * (flat `ImageProxy` for CameraX; stride-aware plane for Camera2/UVC).
     *
     * @param analyzer The service's shared analyzer (holds baseline/score state).
     * @param sensitivity Detection sensitivity threshold.
     * @return `true` if motion was detected in this frame.
     * @since 1.2.0
     */
    public fun analyze(analyzer: MotionAnalyzer, sensitivity: Float): Boolean

    /**
     * Encodes this frame to a JPEG for the MJPEG stream, or returns `null` if encoding fails.
     *
     * Only called when streaming is enabled and the FPS throttle allows it.
     *
     * @param encoder Shared JPEG encoder.
     * @param rotationDegrees Clockwise rotation to apply.
     * @param quality JPEG quality 0–100.
     * @return Encoded JPEG bytes, or `null` on failure.
     * @since 1.2.0
     */
    public fun encodeJpeg(encoder: JpegFrameEncoder, rotationDegrees: Int, quality: Int): ByteArray?

    /**
     * Releases the underlying native buffer. Always called by the service after handling,
     * mirroring CameraX's `ImageProxy.close()` contract.
     *
     * @since 1.2.0
     */
    public fun close()
}
