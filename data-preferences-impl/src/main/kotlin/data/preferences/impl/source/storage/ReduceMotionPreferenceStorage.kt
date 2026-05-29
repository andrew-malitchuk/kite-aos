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

    override fun subscribeToData(): Flow<ReduceMotionDataProto.ReduceMotionProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(ReduceMotionDataProto.ReduceMotionProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): ReduceMotionDataProto.ReduceMotionProtoModel? =
        preference.data.firstOrNull()

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
