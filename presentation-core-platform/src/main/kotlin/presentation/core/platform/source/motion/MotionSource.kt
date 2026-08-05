package presentation.core.platform.source.motion

import android.content.Context
import android.util.Size

/**
 * A source of camera frames for motion detection and MJPEG streaming.
 *
 * The active source is chosen at runtime by [MotionSourceFactory] via capability detection,
 * so the same `MotionService` drives CameraX on mobile, a Camera2 external camera or a UVC
 * webcam on TV, or an MQTT presence topic when no camera exists. Adding a TV-only source is
 * purely additive: it appears in the `src/tv` source set and the factory picks it up by
 * priority. Frame acquisition runs on the source's own thread; each frame is delivered to the
 * `onFrame` callback and the service owns the stateful handling.
 *
 * @see MotionSourceFactory
 * @see MotionFrame
 * @since 1.2.0
 */
public interface MotionSource {

    /**
     * Selection priority; the highest-priority [isAvailable] source wins. Camera2-external
     * (40) > UVC (30) > MQTT (20) on TV; CameraX (50) wins on mobile but reports unavailable
     * on camera-less TV boxes; the no-op fallback (0) is always available last.
     *
     * @since 1.2.0
     */
    public val priority: Int

    /**
     * Hardware category of this source, used by [MotionSourceFactory] to honour an explicit user
     * camera choice: [CameraSourceModel.Front]/[CameraSourceModel.Rear] select a [CameraKind.BUILT_IN]
     * source (the lens is passed through [MotionSourceConfig.lens]), while
     * [CameraSourceModel.External] selects a [CameraKind.EXTERNAL] source. Camera-backed sources
     * default to [CameraKind.BUILT_IN]; external and signal-only sources override this.
     *
     * @since 1.4.0
     */
    public val kind: CameraKind
        get() = CameraKind.BUILT_IN

    /**
     * Whether this source detects motion from a physical camera (front, external, or UVC), as
     * opposed to an external signal such as an MQTT presence topic or the no-op fallback.
     *
     * Used to decide whether the dismissable dark overlay may be shown on Android TV (see
     * `MotionService`): the overlay is only safe when a camera can locally detect motion to
     * clear it. Camera-backed sources inherit the `true` default; non-camera sources override it.
     *
     * @since 1.3.0
     */
    public val isCameraBased: Boolean
        get() = true

    /**
     * Whether this source can operate on the current device right now (hardware present,
     * permission granted, topic configured, …). Evaluated by the factory before selection.
     *
     * @param context Application context for capability queries.
     * @since 1.2.0
     */
    public fun isAvailable(context: Context): Boolean

    /**
     * Begins acquiring frames, delivering each to [onFrame]. The source encodes JPEGs lazily
     * only when [MotionSourceConfig.streamingEnabled] is set.
     *
     * @param config Current motion/streaming configuration.
     * @param onFrame Callback invoked per frame on the source's acquisition thread.
     * @since 1.2.0
     */
    public fun start(config: MotionSourceConfig, onFrame: (MotionFrame) -> Unit)

    /**
     * Applies a new configuration (e.g. streaming toggled, resolution/rotation changed)
     * without a full restart where possible.
     *
     * @since 1.2.0
     */
    public fun updateConfig(config: MotionSourceConfig)

    /**
     * Stops acquisition and releases hardware/threads. Safe to call when already stopped.
     *
     * @since 1.2.0
     */
    public fun stop()
}

/**
 * Hardware category of a [MotionSource], used to match an explicit user camera choice.
 *
 * @since 1.4.0
 */
public enum class CameraKind {
    /** A built-in camera (front or rear), e.g. the CameraX source on mobile. */
    BUILT_IN,

    /** An attached external USB / UVC webcam (Camera2 external or the AUSBC engine). */
    EXTERNAL,

    /** A non-camera source (MQTT presence signal or the no-op fallback). */
    NONE,
}

/**
 * Which built-in lens a [CameraKind.BUILT_IN] source should bind.
 *
 * Only meaningful for built-in cameras; external and signal-only sources ignore it.
 *
 * @since 1.4.0
 */
public enum class CameraLens {
    /** Front-facing (selfie) lens — the historical default. */
    FRONT,

    /** Rear-facing (world) lens. */
    REAR,
}

/**
 * Frame-acquisition configuration passed to a [MotionSource].
 *
 * @property streamingEnabled Whether MJPEG streaming is active (drives higher resolution).
 * @property motionResolution Resolution for motion-only analysis (low, e.g. 176×144).
 * @property streamResolution Resolution used while streaming (e.g. 640×480).
 * @property fps Target streaming frame rate.
 * @property quality Streaming JPEG quality 0–100.
 * @property rotationDegrees Clockwise rotation applied to streamed frames.
 * @property lens Built-in lens to bind (front/rear); ignored by external and signal-only sources.
 * @since 1.2.0
 */
public data class MotionSourceConfig(
    val streamingEnabled: Boolean,
    val motionResolution: Size,
    val streamResolution: Size,
    val fps: Int,
    val quality: Int,
    val rotationDegrees: Int,
    val lens: CameraLens = CameraLens.FRONT,
)
