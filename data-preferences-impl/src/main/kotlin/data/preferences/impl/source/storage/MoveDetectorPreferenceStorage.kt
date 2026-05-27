package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.MoveDetectorDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for motion detector preference data.
 *
 * Wraps the named `"moveDetectorDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [MoveDetectorDataProto.MoveDetectorProtoModel].
 *
 * @param preference the [DataStore] instance for motion detector Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.MoveDetectorProtoSerializer
 * @see data.preferences.impl.core.mapper.MoveDetectorProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.MoveDetectorPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class MoveDetectorPreferenceStorage(
    @Named("moveDetectorDataStore") private val preference: DataStore<MoveDetectorDataProto.MoveDetectorProtoModel>,
) : BasePreferenceStorage<MoveDetectorDataProto.MoveDetectorProtoModel> {

    /**
     * Subscribes to motion detector preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [MoveDetectorDataProto.MoveDetectorProtoModel].
     */
    override fun subscribeToData(): Flow<MoveDetectorDataProto.MoveDetectorProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                // Emit default instance on I/O errors to prevent stream termination
                emit(MoveDetectorDataProto.MoveDetectorProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Retrieves the current motion detector preference data as a single snapshot.
     *
     * @return the current [MoveDetectorDataProto.MoveDetectorProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): MoveDetectorDataProto.MoveDetectorProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the motion detector preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges all motion detector fields (enabled, sensitivity, dimDelay,
     * screenTimeout, fabDelay) into the existing stored model via the builder.
     *
     * @param value the new [MoveDetectorDataProto.MoveDetectorProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: MoveDetectorDataProto.MoveDetectorProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                // Reset to default instance when null is passed
                MoveDetectorDataProto.MoveDetectorProtoModel.getDefaultInstance()
            } else {
                preference.toBuilder().setEnabled(value.enabled).setSensitivity(value.sensitivity)
                    .setDimDelay(value.dimDelay).setScreenTimeout(value.screenTimeout).setFabDelay(value.fabDelay)
                    .build()
            }
        }
    }
}
