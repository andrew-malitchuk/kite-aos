package presentation.core.platform.source.motion

import android.content.Context
import android.util.Log
import domain.core.source.model.CameraSourceModel
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

/**
 * Selects the active [MotionSource] from the user's [CameraSourceModel] choice and runtime
 * capability.
 *
 * Every registered source binds [MotionSource] in Koin, so [getKoin] can enumerate them all.
 * Source-set scoping does the flavor gating (TV-only sources are simply absent from the mobile
 * classpath); this factory does the runtime selection:
 * - [CameraSourceModel.Auto] — highest-priority [MotionSource.isAvailable] source wins (the
 *   historical behaviour): Camera2-external → UVC → MQTT → no-op on TV; CameraX on mobile.
 * - [CameraSourceModel.Front] / [CameraSourceModel.Rear] — the available [CameraKind.BUILT_IN]
 *   source (the lens itself is passed separately via [MotionSourceConfig.lens]).
 * - [CameraSourceModel.External] — the highest-priority available [CameraKind.EXTERNAL] source.
 *
 * Only [CameraSourceModel.Auto] falls back to the best available source. An explicit choice
 * (Front/Rear/External) with no matching available source resolves to [NoOpMotionSource] rather
 * than silently switching to a different camera — the user picked a specific camera, so we don't
 * secretly enable another one (e.g. the built-in front lens) behind their back.
 *
 * @param context Application context used for capability queries.
 * @since 1.2.0
 */
@Single
public class MotionSourceFactory(
    private val context: Context,
) : KoinComponent {

    /**
     * Returns the source matching [choice]. [CameraSourceModel.Auto] picks the best available
     * source; an explicit choice with no matching source resolves to [NoOpMotionSource] instead
     * of switching cameras. Never null — [NoOpMotionSource] is the final fallback.
     *
     * @param choice The user's camera preference; defaults to [CameraSourceModel.Auto].
     * @since 1.4.0
     */
    public fun create(choice: CameraSourceModel = CameraSourceModel.Auto): MotionSource {
        val available = getKoin().getAll<MotionSource>().filter { it.isAvailable(context) }

        val selected = when (choice) {
            // Auto is the only mode that falls back to whatever camera is present.
            CameraSourceModel.Auto ->
                available.maxByOrNull { it.priority }
            CameraSourceModel.Front,
            CameraSourceModel.Rear,
            -> available.filter { it.kind == CameraKind.BUILT_IN }.maxByOrNull { it.priority }
            CameraSourceModel.External ->
                available.filter { it.kind == CameraKind.EXTERNAL }.maxByOrNull { it.priority }
        } ?: NoOpMotionSource()
        Log.i(
            TAG,
            "Selected motion source: ${selected::class.simpleName} " +
                "(choice=$choice, kind=${selected.kind}, priority=${selected.priority})",
        )
        return selected
    }

    private companion object {
        private const val TAG = "MotionSourceFactory"
    }
}
