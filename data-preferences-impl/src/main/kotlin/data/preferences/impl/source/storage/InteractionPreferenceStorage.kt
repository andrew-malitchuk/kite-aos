package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.InteractionDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for the interaction preferences.
 *
 * @param preference the [DataStore] instance injected by name.
 * @see BasePreferenceStorage
 * @since 2.2.0
 */
@Single
internal class InteractionPreferenceStorage(
    @Named("interactionDataStore") private val preference: DataStore<InteractionDataProto.InteractionProtoModel>,
) : BasePreferenceStorage<InteractionDataProto.InteractionProtoModel> {

    /**
     * Subscribes to interaction preference changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance so a corrupt or
     * unreadable file degrades to defaults instead of crashing the kiosk.
     *
     * @return a [Flow] emitting the current [InteractionDataProto.InteractionProtoModel].
     */
    override fun subscribeToData(): Flow<InteractionDataProto.InteractionProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(InteractionDataProto.InteractionProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Retrieves the current interaction preference data as a single snapshot.
     *
     * @return the current [InteractionDataProto.InteractionProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): InteractionDataProto.InteractionProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the interaction preference data in DataStore.
     *
     * @param value the new model to persist, or `null` to reset to defaults.
     */
    override suspend fun updateData(value: InteractionDataProto.InteractionProtoModel?) {
        preference.updateData { current ->
            if (value == null) {
                InteractionDataProto.InteractionProtoModel.getDefaultInstance()
            } else {
                current.toBuilder()
                    .setInactivityResetMinutes(value.inactivityResetMinutes)
                    .setVolumeGestureEnabled(value.volumeGestureEnabled)
                    .setVolumeGesturePressCount(value.volumeGesturePressCount)
                    .build()
            }
        }
    }
}
