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

@Single
internal class ThemePreferenceStorage(
    @Named("themeDataStore")
    private val preference: DataStore<ThemeDataProto.ThemeProtoModel>,
) : BasePreferenceStorage<ThemeDataProto.ThemeProtoModel> {
    override fun subscribeToData(): Flow<ThemeDataProto.ThemeProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(ThemeDataProto.ThemeProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): ThemeDataProto.ThemeProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: ThemeDataProto.ThemeProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
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
