package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.DockDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for dock position preference data.
 *
 * Wraps the named `"dockPositionDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [DockDataProto.DockProtoModel].
 *
 * @param preference the [DataStore] instance for dock Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.DockProtoSerializer
 * @see data.preferences.impl.core.mapper.DockProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.DockPositionPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class DockPreferenceStorage(
    @Named("dockPositionDataStore")
    private val preference: DataStore<DockDataProto.DockProtoModel>,
) : BasePreferenceStorage<DockDataProto.DockProtoModel> {

    /**
     * Subscribes to dock position preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [DockDataProto.DockProtoModel].
     */
    override fun subscribeToData(): Flow<DockDataProto.DockProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            // Emit default instance on I/O errors to prevent stream termination
            emit(DockDataProto.DockProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current dock position preference data as a single snapshot.
     *
     * @return the current [DockDataProto.DockProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): DockDataProto.DockProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the dock position preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the new field values into the existing stored model via the builder.
     *
     * @param value the new [DockDataProto.DockProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: DockDataProto.DockProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                // Reset to default instance when null is passed
                DockDataProto.DockProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setPosition(value.position)
                    .build()
            }
        }
    }
}
