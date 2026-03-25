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

@Single
internal class MoveDetectorPreferenceStorage(
    @Named("moveDetectorDataStore") private val preference: DataStore<MoveDetectorDataProto.MoveDetectorProtoModel>,
) : BasePreferenceStorage<MoveDetectorDataProto.MoveDetectorProtoModel> {
    override fun subscribeToData(): Flow<MoveDetectorDataProto.MoveDetectorProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(MoveDetectorDataProto.MoveDetectorProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): MoveDetectorDataProto.MoveDetectorProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: MoveDetectorDataProto.MoveDetectorProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                MoveDetectorDataProto.MoveDetectorProtoModel.getDefaultInstance()
            } else {
                preference.toBuilder().setEnabled(value.enabled).setSensitivity(value.sensitivity)
                    .setDimDelay(value.dimDelay).setScreenTimeout(value.screenTimeout).setFabDelay(value.fabDelay)
                    .build()
            }
        }
    }
}
