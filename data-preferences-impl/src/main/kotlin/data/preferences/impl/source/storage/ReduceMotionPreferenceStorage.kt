package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.ReduceMotionDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for reduce motion preference data.
 *
 * @param preference the [DataStore] instance injected by name.
 * @see BasePreferenceStorage
 * @since 0.0.6
 */
@Single
internal class ReduceMotionPreferenceStorage(
    @Named("reduceMotionDataStore") private val preference: DataStore<ReduceMotionDataProto.ReduceMotionProtoModel>,
) : BasePreferenceStorage<ReduceMotionDataProto.ReduceMotionProtoModel> {

    /**
     * Subscribes to reduce-motion preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [ReduceMotionDataProto.ReduceMotionProtoModel].
     */
    override fun subscribeToData(): Flow<ReduceMotionDataProto.ReduceMotionProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(ReduceMotionDataProto.ReduceMotionProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Retrieves the current reduce-motion preference data as a single snapshot.
     *
     * @return the current [ReduceMotionDataProto.ReduceMotionProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): ReduceMotionDataProto.ReduceMotionProtoModel? =
        preference.data.firstOrNull()

    /**
     * Updates the reduce-motion preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the enabled field into the existing stored model via the builder.
     *
     * @param value the new [ReduceMotionDataProto.ReduceMotionProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: ReduceMotionDataProto.ReduceMotionProtoModel?) {
        preference.updateData { current ->
            if (value == null) {
                ReduceMotionDataProto.ReduceMotionProtoModel.getDefaultInstance()
            } else {
                current.toBuilder()
                    .setEnabled(value.enabled)
                    .build()
            }
        }
    }
}
