package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.MoveDetectorPreferenceSource
import data.preferences.api.source.resource.MoveDetectorPreference
import data.preferences.impl.core.mapper.MoveDetectorProtobufPreferenceMapper
import data.preferences.impl.proto.MoveDetectorDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.MoveDetectorPreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.annotation.Single

/**
 * Implementation of [MoveDetectorPreferenceSource] backed by Proto DataStore.
 *
 * In addition to the standard preference CRUD operations inherited from [BasePreferenceSourceImpl],
 * this class provides a motion event channel via [MutableSharedFlow]. The [emitMotion] method
 * allows the motion detection service to publish events, while [observeMotion] allows consumers
 * to reactively observe them.
 *
 * The motion flow is configured with no replay and a single-element extra buffer, meaning
 * events are dropped if no subscriber is currently collecting.
 *
 * @param storage the [MoveDetectorPreferenceStorage] providing DataStore access.
 * @see MoveDetectorPreferenceSource
 * @see MoveDetectorPreferenceStorage
 * @see MoveDetectorProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 0.0.1
 */
@Single(binds = [MoveDetectorPreferenceSource::class])
internal class MoveDetectorPreferenceSourceImpl(
    storage: MoveDetectorPreferenceStorage,
) : BasePreferenceSourceImpl<MoveDetectorDataProto.MoveDetectorProtoModel, MoveDetectorPreference>(
    storage = storage,
    mapper = MoveDetectorProtobufPreferenceMapper,
),
    MoveDetectorPreferenceSource {

    /**
     * Internal shared flow for broadcasting motion detection events.
     * Configured with no replay (new subscribers do not receive past events) and a single-element
     * extra buffer to prevent suspension when emitting without active collectors.
     */
    private val motionFlow = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

    /**
     * Observes motion detection events as a hot [Flow].
     *
     * @return a [Flow] that emits [Unit] each time a motion event is detected.
     */
    override fun observeMotion(): Flow<Unit> = motionFlow

    /**
     * Publishes a motion detection event to all active subscribers.
     *
     * If no subscribers are currently collecting, the event is buffered (up to 1 element)
     * and delivered when a subscriber connects; beyond that, events are dropped.
     */
    override suspend fun emitMotion() {
        motionFlow.emit(Unit)
    }
}
