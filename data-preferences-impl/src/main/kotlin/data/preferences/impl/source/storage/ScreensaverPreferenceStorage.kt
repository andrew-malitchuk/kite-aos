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

@Single
internal class ScreensaverPreferenceStorage(
    @Named("screensaverDataStore")
    private val preference: DataStore<ScreensaverDataProto.ScreensaverProtoModel>,
) : BasePreferenceStorage<ScreensaverDataProto.ScreensaverProtoModel> {

    override fun subscribeToData(): Flow<ScreensaverDataProto.ScreensaverProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            emit(ScreensaverDataProto.ScreensaverProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun getData(): ScreensaverDataProto.ScreensaverProtoModel? = preference.data.firstOrNull()

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
