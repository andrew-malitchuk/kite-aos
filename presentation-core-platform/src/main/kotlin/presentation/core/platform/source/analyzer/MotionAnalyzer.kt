package presentation.core.platform.source.analyzer

import android.util.Log
import androidx.camera.core.ImageProxy
import kotlin.math.abs

/**
 * Encapsulates the logic for analyzing camera frames to detect motion.
 *
 * This class uses a combination of frame-to-frame luma difference and a decaying motion score
 * to identify movement while ignoring minor noise or illumination changes.
 */
@Suppress("MagicNumber")
public class MotionAnalyzer {
    private var lastAverageLuma = -1.0
    private var motionScore = 0.0
    private var frameCounter = 0

    private companion object {
        private const val TAG = "MotionAnalyzer"
        private const val FRAME_SKIP_COUNT = 10
        private const val ANALYZER_STEP = 80
        private const val DECAY_FACTOR = 0.5
        private const val MOTION_THRESHOLD = 3.0
    }

    /**
     * Resets the analyzer state.
     */
    public fun reset() {
        lastAverageLuma = -1.0
        motionScore = 0.0
        frameCounter = 0
    }

    /**
     * Analyzes a single [ImageProxy] frame for motion.
     *
     * @param image The frame to analyze.
     * @param sensitivity The sensitivity threshold (typically 1.0 to 10.0).
     * @return true if motion was detected, false otherwise.
     */
    public fun analyze(image: ImageProxy, sensitivity: Float): Boolean {
        try {
            if (++frameCounter % FRAME_SKIP_COUNT != 0) return false

            val buffer = image.planes[0].buffer
            val remaining = buffer.remaining()
            if (remaining == 0) return false

            var sum = 0L
            var pixelCount = 0

            // Sample pixels across the frame to calculate average luma
            for (i in 0 until remaining step ANALYZER_STEP) {
                sum += buffer.get(i).toInt() and 0xFF
                pixelCount++
            }

            val currentAvg = sum.toDouble() / pixelCount

            // Initialization case
            if (lastAverageLuma < 0) {
                lastAverageLuma = currentAvg
                return false
            }

            // Calculate difference from previous frame
            val frameDiff = abs(currentAvg - lastAverageLuma)

            // Update baseline with a low-pass filter
            lastAverageLuma = (lastAverageLuma * 0.9) + (currentAvg * 0.1)

            // Update motion score with decay
            val effectiveDiff = (frameDiff - MOTION_THRESHOLD).coerceAtLeast(0.0)
            motionScore = (motionScore * DECAY_FACTOR) + effectiveDiff

            return motionScore > sensitivity
        } catch (e: Exception) {
            Log.e(TAG, "Frame analysis failed", e)
            return false
        }
    }
}
