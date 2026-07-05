package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.AutoRebootDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for auto-reboot schedule preference data.
 *
 * Wraps the named `"autoRebootDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [AutoRebootDataProto.AutoRebootProtoModel].
 *
 * @param preference the [DataStore] instance for auto-reboot Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.AutoRebootProtoSerializer
 * @see data.preferences.impl.core.mapper.AutoRebootProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.AutoRebootPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class AutoRebootPreferenceStorage(
    @Named("autoRebootDataStore") private val preference: DataStore<AutoRebootDataProto.AutoRebootProtoModel>,
) : BasePreferenceStorage<AutoRebootDataProto.AutoRebootProtoModel> {

    /**
     * Subscribes to auto-reboot preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [AutoRebootDataProto.AutoRebootProtoModel].
     */
    override fun subscribeToData(): Flow<AutoRebootDataProto.AutoRebootProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(AutoRebootDataProto.AutoRebootProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Retrieves the current auto-reboot preference data as a single snapshot.
     *
     * @return the current [AutoRebootDataProto.AutoRebootProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): AutoRebootDataProto.AutoRebootProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the auto-reboot preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges all schedule fields (enabled, hour, minute, intervalDays) into the
     * existing stored model via the builder.
     *
     * @param value the new [AutoRebootDataProto.AutoRebootProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: AutoRebootDataProto.AutoRebootProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                AutoRebootDataProto.AutoRebootProtoModel.getDefaultInstance()
            } else {
                preference.toBuilder()
                    .setEnabled(value.enabled)
                    .setHour(value.hour)
                    .setMinute(value.minute)
                    .setIntervalDays(value.intervalDays)
                    .build()
            }
        }
    }
}
