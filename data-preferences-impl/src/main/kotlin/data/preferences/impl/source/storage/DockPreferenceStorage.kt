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

@Single
internal class DockPreferenceStorage(
    @Named("dockPositionDataStore")
    private val preference: DataStore<DockDataProto.DockProtoModel>,
) : BasePreferenceStorage<DockDataProto.DockProtoModel> {
    override fun subscribeToData(): Flow<DockDataProto.DockProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(DockDataProto.DockProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun getData(): DockDataProto.DockProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: DockDataProto.DockProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
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
