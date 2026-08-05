package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.CameraDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for the camera-source preference.
 *
 * Wraps the named `"cameraDataStore"` [DataStore] instance and implements [BasePreferenceStorage]
 * to provide reactive observation, single-shot retrieval, and update operations for
 * [CameraDataProto.CameraProtoModel].
 *
 * @param preference the [DataStore] instance for camera Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.CameraProtoSerializer
 * @see data.preferences.impl.core.mapper.CameraProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.CameraPreferenceSourceImpl
 * @since 1.4.0
 */
@Single
internal class CameraPreferenceStorage(
    @Named("cameraDataStore")
    private val preference: DataStore<CameraDataProto.CameraProtoModel>,
) : BasePreferenceStorage<CameraDataProto.CameraProtoModel> {

    override fun subscribeToData(): Flow<CameraDataProto.CameraProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(CameraDataProto.CameraProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun getData(): CameraDataProto.CameraProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: CameraDataProto.CameraProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                CameraDataProto.CameraProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setMode(value.mode)
                    .build()
            }
        }
    }
}
