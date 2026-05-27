package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.AutoReturnDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for auto-return preference data.
 *
 * @param preference the [DataStore] instance for auto-return Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.AutoReturnProtoSerializer
 * @see data.preferences.impl.source.datasource.AutoReturnPreferenceSourceImpl
 * @since 0.0.4
 */
@Single
internal class AutoReturnPreferenceStorage(
    @Named("autoReturnDataStore")
    private val preference: DataStore<AutoReturnDataProto.AutoReturnProtoModel>,
) : BasePreferenceStorage<AutoReturnDataProto.AutoReturnProtoModel> {

    override fun subscribeToData(): Flow<AutoReturnDataProto.AutoReturnProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(AutoReturnDataProto.AutoReturnProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun getData(): AutoReturnDataProto.AutoReturnProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: AutoReturnDataProto.AutoReturnProtoModel?) {
        preference.updateData { current ->
            if (value == null) {
                AutoReturnDataProto.AutoReturnProtoModel.getDefaultInstance()
            } else {
                current
                    .toBuilder()
                    .setIsEnabled(value.isEnabled)
                    .build()
            }
        }
    }
}
