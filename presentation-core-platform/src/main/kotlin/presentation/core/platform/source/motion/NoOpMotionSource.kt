package presentation.core.platform.source.motion

import android.content.Context
import org.koin.core.annotation.Single

/**
 * Last-resort motion source that produces no frames.
 *
 * Selected only when no camera and no MQTT fallback is available (e.g. a camera-less TV box
 * with no PIR sensor configured). It keeps the foreground service alive so device power
 * management (wake/dim/lock) still functions, while motion detection and streaming are inert.
 *
 * @since 1.2.0
 */
@Single(binds = [MotionSource::class])
public class NoOpMotionSource : MotionSource {

    override val priority: Int = PRIORITY

    override val kind: CameraKind = CameraKind.NONE

    // Not camera-backed: produces no frames, so it can never dismiss a dark overlay.
    override val isCameraBased: Boolean = false

    override fun isAvailable(context: Context): Boolean = true

    override fun start(config: MotionSourceConfig, onFrame: (MotionFrame) -> Unit) {
        // No frames produced.
    }

    override fun updateConfig(config: MotionSourceConfig) {
        // Nothing to reconfigure.
    }

    override fun stop() {
        // Nothing to release.
    }

    private companion object {
        private const val PRIORITY = 0
    }
}
