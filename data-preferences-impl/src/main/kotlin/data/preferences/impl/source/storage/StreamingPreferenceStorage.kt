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

@Single
internal class StreamingPreferenceStorage(
    @Named("streamingDataStore")
    private val preference: DataStore<StreamingDataProto.StreamingProtoModel>,
) : BasePreferenceStorage<StreamingDataProto.StreamingProtoModel> {

    override fun subscribeToData(): Flow<StreamingDataProto.StreamingProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(StreamingDataProto.StreamingProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun getData(): StreamingDataProto.StreamingProtoModel? = preference.data.firstOrNull()

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
                    .build()
            }
        }
    }
}
