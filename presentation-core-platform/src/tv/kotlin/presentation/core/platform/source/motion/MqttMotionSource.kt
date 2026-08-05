package presentation.core.platform.source.motion

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Single
import presentation.core.platform.source.analyzer.MotionAnalyzer
import presentation.core.platform.source.command.RemoteCommandBus
import presentation.core.platform.source.streaming.JpegFrameEncoder

/**
 * TV motion fallback that turns external motion pulses into motion events.
 *
 * Used on camera-less TV boxes: a presence source (e.g. a PIR sensor via a Home Assistant
 * automation publishing to the device's motion-set topic) drives [RemoteCommandBus.motion],
 * and each pulse becomes a synthetic [MotionFrame] whose analysis reports motion — reusing the
 * exact same `MotionService` handling as a real camera frame (wake/dim reset, MQTT presence,
 * screensaver dismiss). Produces no video, so MJPEG streaming is inactive under this source.
 *
 * It consumes the bus rather than the MQTT use case directly: injecting the cross-module use
 * case into this `@Single` makes koin-annotations skip KSP generation for the whole tv variant.
 * `MqttService` (a Service, not a `@Single`) safely bridges the use case onto the bus.
 *
 * Lowest camera-less priority above the no-op fallback: selected on TV only when neither the
 * Camera2-external nor the UVC source is available.
 *
 * @param remoteCommandBus In-process bus carrying external motion pulses.
 * @since 1.2.0
 */
@Single(binds = [MotionSource::class])
public class MqttMotionSource(
    private val remoteCommandBus: RemoteCommandBus,
) : MotionSource {

    private var scope: CoroutineScope? = null

    override val priority: Int = PRIORITY

    override val kind: CameraKind = CameraKind.NONE

    // Not camera-backed: presence comes from an MQTT topic, so there is no local frame stream
    // that can dismiss a dark overlay.
    override val isCameraBased: Boolean = false

    // Always a candidate on TV; the factory only reaches it when no camera source is available.
    override fun isAvailable(context: Context): Boolean = true

    override fun start(config: MotionSourceConfig, onFrame: (MotionFrame) -> Unit) {
        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        scope = newScope
        remoteCommandBus.motion
            .onEach { onFrame(MqttMotionFrame) }
            .launchIn(newScope)
    }

    override fun updateConfig(config: MotionSourceConfig) {
        // No hardware to reconfigure; streaming is not supported for this source.
    }

    override fun stop() {
        scope?.cancel()
        scope = null
    }

    private companion object {
        private const val PRIORITY = 20
    }
}

/**
 * A frameless motion pulse: reports motion unconditionally and produces no JPEG.
 *
 * Reusing the [MotionFrame] contract lets external presence flow through the identical
 * `MotionService` handler as camera frames.
 *
 * @since 1.2.0
 */
internal object MqttMotionFrame : MotionFrame {
    override fun analyze(analyzer: MotionAnalyzer, sensitivity: Float): Boolean = true

    override fun encodeJpeg(encoder: JpegFrameEncoder, rotationDegrees: Int, quality: Int): ByteArray? = null

    override fun close() {
        // No native buffer to release.
    }
}
