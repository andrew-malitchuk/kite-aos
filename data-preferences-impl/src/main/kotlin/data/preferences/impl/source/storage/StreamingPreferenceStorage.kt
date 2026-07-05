package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.StreamingDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for camera streaming preference data.
 *
 * Wraps the named `"streamingDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [StreamingDataProto.StreamingProtoModel].
 *
 * @param preference the [DataStore] instance for streaming Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.StreamingProtoSerializer
 * @see data.preferences.impl.core.mapper.StreamingProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.StreamingPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class StreamingPreferenceStorage(
    @Named("streamingDataStore")
    private val preference: DataStore<StreamingDataProto.StreamingProtoModel>,
) : BasePreferenceStorage<StreamingDataProto.StreamingProtoModel> {

    /**
     * Subscribes to camera streaming preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [StreamingDataProto.StreamingProtoModel].
     */
    override fun subscribeToData(): Flow<StreamingDataProto.StreamingProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(StreamingDataProto.StreamingProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current camera streaming preference data as a single snapshot.
     *
     * @return the current [StreamingDataProto.StreamingProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): StreamingDataProto.StreamingProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the camera streaming preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges all streaming fields (enabled, port, quality, fps, rotation) into
     * the existing stored model via the builder.
     *
     * @param value the new [StreamingDataProto.StreamingProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: StreamingDataProto.StreamingProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                StreamingDataProto.StreamingProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setEnabled(value.enabled)
                    .setPort(value.port)
                    .setQuality(value.quality)
                    .setFps(value.fps)
                    .setRotation(value.rotation)
                    .build()
            }
        }
    }
}
