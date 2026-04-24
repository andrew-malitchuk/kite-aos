package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.ThemeDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for theme preference data.
 *
 * Wraps the named `"themeDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [ThemeDataProto.ThemeProtoModel].
 *
 * @param preference the [DataStore] instance for theme Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.ThemeProtoSerializer
 * @see data.preferences.impl.core.mapper.ThemeProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.ThemePreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class ThemePreferenceStorage(
    @Named("themeDataStore")
    private val preference: DataStore<ThemeDataProto.ThemeProtoModel>,
) : BasePreferenceStorage<ThemeDataProto.ThemeProtoModel> {

    /**
     * Subscribes to theme preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [ThemeDataProto.ThemeProtoModel].
     */
    override fun subscribeToData(): Flow<ThemeDataProto.ThemeProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            // Emit default instance on I/O errors to prevent stream termination
            emit(ThemeDataProto.ThemeProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current theme preference data as a single snapshot.
     *
     * @return the current [ThemeDataProto.ThemeProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): ThemeDataProto.ThemeProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the theme preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the theme mode into the existing stored model via the builder.
     *
     * @param value the new [ThemeDataProto.ThemeProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: ThemeDataProto.ThemeProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                // Reset to default instance when null is passed
                ThemeDataProto.ThemeProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setMode(value.mode)
                    .build()
            }
        }
    }
}
