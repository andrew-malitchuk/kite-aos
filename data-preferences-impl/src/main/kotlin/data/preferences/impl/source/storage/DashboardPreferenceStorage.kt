package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.DashboardDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for dashboard preference data.
 *
 * Wraps the named `"dashboardDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [DashboardDataProto.DashboardProtoModel].
 *
 * @param preference the [DataStore] instance for dashboard Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.DashboardProtoSerializer
 * @see data.preferences.impl.core.mapper.DashboardProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.DashboardPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class DashboardPreferenceStorage(
    @Named("dashboardDataStore")
    private val preference: DataStore<DashboardDataProto.DashboardProtoModel>,
) : BasePreferenceStorage<DashboardDataProto.DashboardProtoModel> {

    /**
     * Subscribes to dashboard preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [DashboardDataProto.DashboardProtoModel].
     */
    override fun subscribeToData(): Flow<DashboardDataProto.DashboardProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            // Emit default instance on I/O errors to prevent stream termination
            emit(DashboardDataProto.DashboardProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current dashboard preference data as a single snapshot.
     *
     * @return the current [DashboardDataProto.DashboardProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): DashboardDataProto.DashboardProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the dashboard preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the new field values into the existing stored model via the builder.
     *
     * @param value the new [DashboardDataProto.DashboardProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: DashboardDataProto.DashboardProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                // Reset to default instance when null is passed
                DashboardDataProto.DashboardProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setDashboardUrl(value.dashboardUrl)
                    .setWhitelistUrl(value.whitelistUrl)
                    .build()
            }
        }
    }
}
