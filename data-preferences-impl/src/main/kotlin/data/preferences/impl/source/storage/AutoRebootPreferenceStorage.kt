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

@Single
internal class AutoRebootPreferenceStorage(
    @Named("autoRebootDataStore") private val preference: DataStore<AutoRebootDataProto.AutoRebootProtoModel>,
) : BasePreferenceStorage<AutoRebootDataProto.AutoRebootProtoModel> {

    override fun subscribeToData(): Flow<AutoRebootDataProto.AutoRebootProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(AutoRebootDataProto.AutoRebootProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): AutoRebootDataProto.AutoRebootProtoModel? = preference.data.firstOrNull()

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
