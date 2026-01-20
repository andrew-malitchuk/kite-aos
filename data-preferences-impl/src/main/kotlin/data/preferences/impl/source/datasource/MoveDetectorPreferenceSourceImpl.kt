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

@Single(binds = [MoveDetectorPreferenceSource::class])
internal class MoveDetectorPreferenceSourceImpl(
    storage: MoveDetectorPreferenceStorage,
) : BasePreferenceSourceImpl<MoveDetectorDataProto.MoveDetectorProtoModel, MoveDetectorPreference>(
    storage = storage,
    mapper = MoveDetectorProtobufPreferenceMapper,
), MoveDetectorPreferenceSource {

    private val motionFlow = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

    override fun observeMotion(): Flow<Unit> = motionFlow

    override suspend fun emitMotion() {
        motionFlow.emit(Unit)
    }
}
