package data.runtime.impl.source.datasource

import data.runtime.api.source.datasource.ScreenStateSource
import data.runtime.api.source.resource.ScreenStateResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

/**
 * In-memory implementation of [ScreenStateSource] backed by a [MutableSharedFlow].
 *
 * Maintains the latest screen display state as a hot, replay-1 shared flow so that
 * new collectors immediately receive the current state without waiting for the next emission.
 *
 * The buffer holds up to 4 extra elements beyond the replayed item. When the buffer is full,
 * [BufferOverflow.DROP_OLDEST] evicts the oldest pending state rather than suspending the emitter,
 * ensuring background services (e.g., MQTT, motion detection) are never blocked.
 *
 * @see ScreenStateSource
 * @since 1.1.0
 */
@Single(binds = [ScreenStateSource::class])
internal class ScreenStateSourceImpl : ScreenStateSource {

    /**
     * The backing shared flow holding the current [ScreenStateResource].
     *
     * Initialized with [ScreenStateResource.Active] so every collector receives an
     * immediate, non-null value upon subscription.
     */
    private val _state = MutableSharedFlow<ScreenStateResource>(
        replay = 1,
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    ).also { it.tryEmit(ScreenStateResource.Active) }

    override fun observe(): Flow<ScreenStateResource> = _state.asSharedFlow()

    override suspend fun emit(state: ScreenStateResource) {
        _state.emit(state)
    }
}
