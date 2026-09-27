package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.ResilienceDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for the unattended-operation safeguard preferences.
 *
 * @param preference the [DataStore] instance injected by name.
 * @see BasePreferenceStorage
 * @since 2.2.0
 */
@Single
internal class ResiliencePreferenceStorage(
    @Named("resilienceDataStore") private val preference: DataStore<ResilienceDataProto.ResilienceProtoModel>,
) : BasePreferenceStorage<ResilienceDataProto.ResilienceProtoModel> {

    /**
     * Subscribes to resilience preference changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance so a corrupt or
     * unreadable file degrades to defaults instead of crashing the kiosk.
     *
     * @return a [Flow] emitting the current [ResilienceDataProto.ResilienceProtoModel].
     */
    override fun subscribeToData(): Flow<ResilienceDataProto.ResilienceProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(ResilienceDataProto.ResilienceProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Retrieves the current resilience preference data as a single snapshot.
     *
     * @return the current [ResilienceDataProto.ResilienceProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): ResilienceDataProto.ResilienceProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the resilience preference data in DataStore.
     *
     * @param value the new model to persist, or `null` to reset to defaults.
     */
    override suspend fun updateData(value: ResilienceDataProto.ResilienceProtoModel?) {
        preference.updateData { current ->
            if (value == null) {
                ResilienceDataProto.ResilienceProtoModel.getDefaultInstance()
            } else {
                current.toBuilder()
                    .setCrashRelaunchDisabled(value.crashRelaunchDisabled)
                    .setScheduledReloadEnabled(value.scheduledReloadEnabled)
                    .setScheduledReloadHourPlusOne(value.scheduledReloadHourPlusOne)
                    .setMemoryRecoveryDisabled(value.memoryRecoveryDisabled)
                    .setConnectionMonitorDisabled(value.connectionMonitorDisabled)
                    .setWifiLockDisabled(value.wifiLockDisabled)
                    .build()
            }
        }
    }
}
