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

/**
 * Proto DataStore-backed storage for language/locale preference data.
 *
 * Wraps the named `"languageDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [LanguagePreferenceProto].
 *
 * @param preference the [DataStore] instance for language Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.LanguageProtoSerializer
 * @see data.preferences.impl.core.mapper.LanguageProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.LanguagePreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class LanguagePreferenceStorage(
    @Named("languageDataStore")
    private val preference: DataStore<LanguagePreferenceProto>,
) : BasePreferenceStorage<LanguagePreferenceProto> {

    /**
     * Subscribes to language preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [LanguagePreferenceProto].
     */
    override fun subscribeToData(): Flow<LanguagePreferenceProto?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            // Emit default instance on I/O errors to prevent stream termination
            emit(LanguagePreferenceProto.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current language preference data as a single snapshot.
     *
     * @return the current [LanguagePreferenceProto], or `null` if unavailable.
     */
    override suspend fun getData(): LanguagePreferenceProto? = preference.data.firstOrNull()

    /**
     * Updates the language preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the new locale code into the existing stored model via the builder.
     *
     * @param value the new [LanguagePreferenceProto] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: LanguagePreferenceProto?) {
        preference.updateData { preference ->
            if (value == null) {
                // Reset to default instance when null is passed
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
