package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.ScreensaverDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for screensaver preference data.
 *
 * Wraps the named `"screensaverDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [ScreensaverDataProto.ScreensaverProtoModel].
 *
 * @param preference the [DataStore] instance for screensaver Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.ScreensaverProtoSerializer
 * @see data.preferences.impl.core.mapper.ScreensaverProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.ScreensaverPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class ScreensaverPreferenceStorage(
    @Named("screensaverDataStore")
    private val preference: DataStore<ScreensaverDataProto.ScreensaverProtoModel>,
) : BasePreferenceStorage<ScreensaverDataProto.ScreensaverProtoModel> {

    /**
     * Subscribes to screensaver preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [ScreensaverDataProto.ScreensaverProtoModel].
     */
    override fun subscribeToData(): Flow<ScreensaverDataProto.ScreensaverProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(ScreensaverDataProto.ScreensaverProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current screensaver preference data as a single snapshot.
     *
     * @return the current [ScreensaverDataProto.ScreensaverProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): ScreensaverDataProto.ScreensaverProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the screensaver preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges all screensaver fields (enabled, activationDelay, slideInterval, showClock,
     * source, localFolderUri) into the existing stored model via the builder.
     *
     * @param value the new [ScreensaverDataProto.ScreensaverProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: ScreensaverDataProto.ScreensaverProtoModel?) {
        preference.updateData { pref ->
            if (value == null) {
                ScreensaverDataProto.ScreensaverProtoModel.getDefaultInstance()
            } else {
                pref.toBuilder()
                    .setEnabled(value.enabled)
                    .setActivationDelay(value.activationDelay)
                    .setSlideInterval(value.slideInterval)
                    .setShowClock(value.showClock)
                    .setSource(value.source)
                    .setLocalFolderUri(value.localFolderUri)
                    .build()
            }
        }
    }
}
