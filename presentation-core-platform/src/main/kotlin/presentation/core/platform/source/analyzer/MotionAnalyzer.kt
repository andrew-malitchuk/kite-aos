package presentation.core.platform.source.analyzer

import android.util.Log
import androidx.camera.core.ImageProxy
import kotlin.math.abs

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
        } catch (e: Exception) {
            Log.e(TAG, "Frame analysis failed", e)
            return false
        }
    }
}
