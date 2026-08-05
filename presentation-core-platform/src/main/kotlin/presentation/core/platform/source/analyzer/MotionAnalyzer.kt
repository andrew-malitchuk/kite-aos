package presentation.core.platform.source.analyzer

import android.util.Log
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Encapsulates the logic for analyzing camera frames to detect motion.
 *
 * This class uses a combination of frame-to-frame luma difference and a decaying motion score
 * to identify movement while ignoring minor noise or illumination changes. The algorithm works
 * as follows:
 *
 * 1. Sample pixels from the Y (luma) plane at fixed intervals to compute an average brightness.
 * 2. Compare the current average against a low-pass-filtered baseline from previous frames.
 * 3. Accumulate the difference into a decaying motion score.
 * 4. Report motion when the score exceeds the caller-provided sensitivity threshold.
 *
 * @see presentation.core.platform.source.service.MotionService
 * @since 0.0.1
 */
// Suppress MagicNumber at class level — the constants below (0.9, 0.1, 0xFF, etc.) are
// algorithm-specific tuning parameters, not arbitrary magic numbers.
@Suppress("MagicNumber")
public class MotionAnalyzer {
    /** Previous frame's average luma. A value of -1.0 indicates no baseline has been set yet. */
    private var lastAverageLuma = -1.0

    /** Accumulated motion score that decays over time via [DECAY_FACTOR]. */
    private var motionScore = 0.0

    /** Running count of frames received; used for frame-skipping logic. */
    private var frameCounter = 0

    private companion object {
        private const val TAG = "MotionAnalyzer"

        /** Process every Nth frame to reduce CPU load. */
        private const val FRAME_SKIP_COUNT = 10

        /**
         * Step size (in bytes) when sampling the luma buffer. A larger step means fewer pixels
         * are sampled, trading accuracy for speed. At 176x144 resolution (~25 KB Y-plane),
         * a step of 80 yields roughly 316 samples per frame.
         */
        private const val ANALYZER_STEP = 80

        /** Exponential decay factor applied to the motion score each frame (range 0.0–1.0). */
        private const val DECAY_FACTOR = 0.5

        /**
         * Minimum luma difference required before it contributes to the motion score.
         * Differences below this threshold are treated as sensor noise.
         */
        private const val MOTION_THRESHOLD = 3.0
    }

    /**
     * Resets all internal state, clearing the luma baseline and accumulated motion score.
     *
     * This should be called after brightness or screen state changes to prevent false positives
     * caused by abrupt illumination shifts (the "blindness" window in [MotionService]).
     *
     * @see presentation.core.platform.source.service.MotionService
     * @since 0.0.1
     */
    public fun reset() {
        lastAverageLuma = -1.0
        motionScore = 0.0
        frameCounter = 0
    }

    /**
     * Analyzes a single [ImageProxy] frame for motion.
     *
     * The caller is responsible for closing the [image] after this method returns. Internally,
     * only every [FRAME_SKIP_COUNT]-th frame is actually processed to conserve CPU.
     *
     * @param image The CameraX [ImageProxy] frame to analyze. Only the first plane (Y/luma) is
     * used.
     * @param sensitivity The sensitivity threshold that the accumulated motion score must exceed
     * for motion to be reported. Typical values range from 1.0 (very sensitive) to 10.0
     * (less sensitive).
     * @return `true` if motion was detected in this frame, `false` otherwise (including skipped
     * frames and initialization frames).
     * @see reset
     * @since 0.0.1
     */
    public fun analyze(image: ImageProxy, sensitivity: Float): Boolean {
        try {
            // Skip frames to reduce CPU usage — only process every FRAME_SKIP_COUNT-th frame.
            if (++frameCounter % FRAME_SKIP_COUNT != 0) return false

            // CameraX ImageProxy planes[0] is the Y (luminance) plane in YUV format.
            // This path is intentionally left flat (ignoring rowStride) so the mobile
            // CameraX behaviour is byte-for-byte unchanged. The stride-aware overload
            // below is used by the TV frame sources.
            val buffer = image.planes[0].buffer
            val remaining = buffer.remaining()
            if (remaining == 0) return false

            var sum = 0L
            var pixelCount = 0

            // Sample pixels across the frame to calculate average luma.
            // The bitmask 0xFF converts the signed byte to an unsigned int (0–255).
            for (i in 0 until remaining step ANALYZER_STEP) {
                sum += buffer.get(i).toInt() and 0xFF
                pixelCount++
            }

            val currentAvg = sum.toDouble() / pixelCount
            return scoreFrame(currentAvg, sensitivity)
        } catch (e: Exception) {
            Log.e(TAG, "Frame analysis failed", e)
            return false
        }
    }

    /**
     * Analyzes a raw Y (luminance) plane for motion, honoring row/pixel strides.
     *
     * Shares the exact scoring algorithm with the [ImageProxy] overload but samples in a
     * stride-aware 2D grid, so it works for both contiguous NV21 buffers (UVC webcams,
     * `rowStride == width`, `pixelStride == 1`) and row-padded `YUV_420_888` planes
     * (Camera2 external cameras, where `rowStride > width` and padding bytes must be
     * skipped). The caller owns the buffer's lifecycle.
     *
     * @param yPlane The Y-plane bytes. Read by absolute index; position is not modified.
     * @param rowStride Bytes between the start of consecutive rows (≥ [width]).
     * @param pixelStride Bytes between consecutive luma samples in a row (1 for NV21).
     * @param width Frame width in pixels.
     * @param height Frame height in pixels.
     * @param sensitivity Threshold the accumulated motion score must exceed. See the
     * [ImageProxy] overload.
     * @return `true` if motion was detected, `false` otherwise (including skipped and
     * initialization frames).
     * @see reset
     * @since 1.2.0
     */
    public fun analyze(
        yPlane: ByteBuffer,
        rowStride: Int,
        pixelStride: Int,
        width: Int,
        height: Int,
        sensitivity: Float,
    ): Boolean {
        try {
            if (++frameCounter % FRAME_SKIP_COUNT != 0) return false
            if (width <= 0 || height <= 0) return false

            // Sample on a square grid whose spacing matches the flat ANALYZER_STEP density
            // (step ≈ sqrt(ANALYZER_STEP)), so sensitivity is comparable to the mobile path.
            val gridStep = sqrt(ANALYZER_STEP.toDouble()).roundToInt().coerceAtLeast(1)

            var sum = 0L
            var pixelCount = 0
            var row = 0
            while (row < height) {
                val rowStart = row * rowStride
                var col = 0
                while (col < width) {
                    val index = rowStart + col * pixelStride
                    if (index >= yPlane.limit()) break
                    sum += yPlane.get(index).toInt() and 0xFF
                    pixelCount++
                    col += gridStep
                }
                row += gridStep
            }

            if (pixelCount == 0) return false
            val currentAvg = sum.toDouble() / pixelCount
            return scoreFrame(currentAvg, sensitivity)
        } catch (e: Exception) {
            Log.e(TAG, "Frame analysis failed", e)
            return false
        }
    }

    /**
     * Applies the low-pass baseline filter and decaying motion score to a frame's average
     * luma. Shared by both [analyze] overloads so the detection algorithm stays in one place.
     *
     * @param currentAvg The average luma of the current frame.
     * @param sensitivity The threshold the motion score must exceed.
     * @return `true` if motion is detected; `false` on the first (baseline) frame.
     */
    private fun scoreFrame(currentAvg: Double, sensitivity: Float): Boolean {
        // First frame after reset — store baseline and skip detection.
        if (lastAverageLuma < 0) {
            lastAverageLuma = currentAvg
            return false
        }

        // Calculate difference from previous frame's average luma.
        val frameDiff = abs(currentAvg - lastAverageLuma)

        // Update baseline with a low-pass filter: 90% old value + 10% new value.
        // This smooths out gradual illumination changes (e.g., clouds passing).
        lastAverageLuma = (lastAverageLuma * 0.9) + (currentAvg * 0.1)

        // Update motion score with exponential decay. Only differences above the noise
        // threshold contribute to the score.
        val effectiveDiff = (frameDiff - MOTION_THRESHOLD).coerceAtLeast(0.0)
        motionScore = (motionScore * DECAY_FACTOR) + effectiveDiff

        return motionScore > sensitivity
    }
}
