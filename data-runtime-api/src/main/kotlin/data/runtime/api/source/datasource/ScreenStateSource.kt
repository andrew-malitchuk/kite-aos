package data.runtime.api.source.datasource

import data.runtime.api.source.resource.ScreenStateResource
import kotlinx.coroutines.flow.Flow

/**
 * In-memory runtime data source for the current screen display state.
 *
 * This source acts as a hot observable bus for [ScreenStateResource] transitions.
 * It is intended to be used as a singleton shared between background services and the UI layer,
 * enabling state changes (e.g., activating the screensaver) to be propagated without
 * writing to disk.
 *
 * Implementations must emit [ScreenStateResource.Active] as their initial state upon creation.
 *
 * @see ScreenStateResource
 * @see data.runtime.impl.source.datasource.ScreenStateSourceImpl
 * @since 1.1.0
 */
public interface ScreenStateSource {

    /**
     * Returns a [Flow] that emits the current [ScreenStateResource] and any subsequent changes.
     *
     * The flow replays the most recent state to new collectors, so they always receive
     * an immediate value upon subscription.
     *
     * @return A hot [Flow] of [ScreenStateResource] that never completes.
     */
    public fun observe(): Flow<ScreenStateResource>

    /**
     * Emits a new [state] to all active collectors of [observe].
     *
     * This is a suspending function to support structured concurrency; callers should
     * invoke it from a coroutine scope tied to the appropriate lifecycle.
     *
     * @param state The new [ScreenStateResource] to broadcast.
     */
    public suspend fun emit(state: ScreenStateResource)
}
