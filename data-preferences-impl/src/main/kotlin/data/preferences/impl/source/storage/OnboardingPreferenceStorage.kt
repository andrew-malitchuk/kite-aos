package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.OnboardingDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

@Single
internal class OnboardingPreferenceStorage(
    @Named("onboardingDataStore")
    private val preference: DataStore<OnboardingDataProto.OnboardingProtoModel>,
) : BasePreferenceStorage<OnboardingDataProto.OnboardingProtoModel> {
    override fun subscribeToData(): Flow<OnboardingDataProto.OnboardingProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(OnboardingDataProto.OnboardingProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): OnboardingDataProto.OnboardingProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: OnboardingDataProto.OnboardingProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                OnboardingDataProto.OnboardingProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setIsCompleted(value.isCompleted)
                    .build()
            }
        }
    }
}
