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

@Single
internal class DashboardPreferenceStorage(
    @Named("dashboardDataStore")
    private val preference: DataStore<DashboardDataProto.DashboardProtoModel>,
) : BasePreferenceStorage<DashboardDataProto.DashboardProtoModel> {
    override fun subscribeToData(): Flow<DashboardDataProto.DashboardProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(DashboardDataProto.DashboardProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): DashboardDataProto.DashboardProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: DashboardDataProto.DashboardProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
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