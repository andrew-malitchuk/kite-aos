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

    /**
     * Subscribes to browser engine preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [WebEngineDataProto.WebEngineProtoModel].
     */
    override fun subscribeToData(): Flow<WebEngineDataProto.WebEngineProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(WebEngineDataProto.WebEngineProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current browser engine preference data as a single snapshot.
     *
     * @return the current [WebEngineDataProto.WebEngineProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): WebEngineDataProto.WebEngineProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the browser engine preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the engine identifier into the existing stored model via the builder.
     *
     * @param value the new [WebEngineDataProto.WebEngineProtoModel] to persist, or `null` to reset.
     */
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
