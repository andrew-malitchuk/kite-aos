package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.LanguagePreferenceProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

@Single
internal class LanguagePreferenceStorage(
    @Named("languageDataStore")
    private val preference: DataStore<LanguagePreferenceProto>,
) : BasePreferenceStorage<LanguagePreferenceProto> {
    override fun subscribeToData(): Flow<LanguagePreferenceProto?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(LanguagePreferenceProto.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): LanguagePreferenceProto? = preference.data.firstOrNull()

    override suspend fun updateData(value: LanguagePreferenceProto?) {
        preference.updateData { preference ->
            if (value == null) {
                LanguagePreferenceProto.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setLocaleCode(value.localeCode)
                    .build()
            }
        }
    }
}
