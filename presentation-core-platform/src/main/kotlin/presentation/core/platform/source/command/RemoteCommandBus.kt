package presentation.core.platform.source.command

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

/**
 * In-process event bus for hardware-remote (D-pad) commands that originate outside
 * the feature composition tree.
 *
 * On Android TV the settings-bar gesture is a long-press key combo detected in
 * `HostActivity.dispatchKeyEvent` — above the navigation graph and the Main feature.
 * This bus carries that intent to `MainViewModel`, mirroring how the MQTT FAB command
 * reaches the same screen, without threading an Activity callback through navigation.
 *
 * A small buffer with no replay is used: emissions are transient user actions, only
 * meaningful while the kiosk screen is active and collecting.
 *
 * @since 1.2.0
 */
@Single
public class RemoteCommandBus {

    private val _openDrawer = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions requesting the control drawer (settings bar) be opened.
     *
     * @since 1.2.0
     */
    public val openDrawer: SharedFlow<Unit> = _openDrawer.asSharedFlow()

    private val _motion = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emissions representing an external motion pulse (e.g. an MQTT presence command).
     *
     * Kept here rather than injecting the MQTT use case directly into the TV motion source,
     * which would otherwise break koin-annotations KSP generation for the tv variant.
     *
     * @since 1.2.0
     */
    public val motion: SharedFlow<Unit> = _motion.asSharedFlow()

    /**
     * Requests that the control drawer open. Safe to call from any thread.
     *
     * @since 1.2.0
     */
    public fun emitOpenDrawer() {
        _openDrawer.tryEmit(Unit)
    }

    /**
     * Signals an external motion pulse. Safe to call from any thread.
     *
     * @since 1.2.0
     */
    public fun emitMotion() {
        _motion.tryEmit(Unit)
    }
}
