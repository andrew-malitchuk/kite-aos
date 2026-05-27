package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.WebEngineDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for browser engine preference data.
 *
 * @param preference the [DataStore] instance for web engine Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.WebEngineProtoSerializer
 * @see data.preferences.impl.source.datasource.WebEnginePreferenceSourceImpl
 * @since 0.0.4
 */
@Single
internal class WebEnginePreferenceStorage(
    @Named("webEngineDataStore")
    private val preference: DataStore<WebEngineDataProto.WebEngineProtoModel>,
) : BasePreferenceStorage<WebEngineDataProto.WebEngineProtoModel> {

    override fun subscribeToData(): Flow<WebEngineDataProto.WebEngineProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(WebEngineDataProto.WebEngineProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun getData(): WebEngineDataProto.WebEngineProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: WebEngineDataProto.WebEngineProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                WebEngineDataProto.WebEngineProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setEngine(value.engine)
                    .build()
            }
        }
    }
}
