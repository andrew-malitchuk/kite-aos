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

/**
 * Proto DataStore-backed storage for onboarding preference data.
 *
 * Wraps the named `"onboardingDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [OnboardingDataProto.OnboardingProtoModel].
 *
 * @param preference the [DataStore] instance for onboarding Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.OnboardingProtoSerializer
 * @see data.preferences.impl.core.mapper.OnboardingProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.OnboardingPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class OnboardingPreferenceStorage(
    @Named("onboardingDataStore")
    private val preference: DataStore<OnboardingDataProto.OnboardingProtoModel>,
) : BasePreferenceStorage<OnboardingDataProto.OnboardingProtoModel> {

    /**
     * Subscribes to onboarding preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [OnboardingDataProto.OnboardingProtoModel].
     */
    override fun subscribeToData(): Flow<OnboardingDataProto.OnboardingProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                // Emit default instance on I/O errors to prevent stream termination
                emit(OnboardingDataProto.OnboardingProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Retrieves the current onboarding preference data as a single snapshot.
     *
     * @return the current [OnboardingDataProto.OnboardingProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): OnboardingDataProto.OnboardingProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the onboarding preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the completion state into the existing stored model via the builder.
     *
     * @param value the new [OnboardingDataProto.OnboardingProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: OnboardingDataProto.OnboardingProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                // Reset to default instance when null is passed
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
