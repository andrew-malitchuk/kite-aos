package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.MqttDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for MQTT broker preference data.
 *
 * Wraps the named `"mqttDataStore"` [DataStore] instance and implements
 * [BasePreferenceStorage] to provide reactive observation, single-shot retrieval, and
 * update operations for [MqttDataProto.MqttProtoModel].
 *
 * @param preference the [DataStore] instance for MQTT Protobuf models, injected by name.
 * @see BasePreferenceStorage
 * @see data.preferences.impl.core.serializer.MqttProtoSerializer
 * @see data.preferences.impl.core.mapper.MqttProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.MqttPreferenceSourceImpl
 * @since 0.0.1
 */
@Single
internal class MqttPreferenceStorage(
    @Named("mqttDataStore")
    private val preference: DataStore<MqttDataProto.MqttProtoModel>,
) : BasePreferenceStorage<MqttDataProto.MqttProtoModel> {

    /**
     * Subscribes to MQTT preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [MqttDataProto.MqttProtoModel].
     */
    override fun subscribeToData(): Flow<MqttDataProto.MqttProtoModel?> = preference.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Error", exception.message.toString())
            // Emit default instance on I/O errors to prevent stream termination
            emit(MqttDataProto.MqttProtoModel.getDefaultInstance())
        } else {
            throw exception
        }
    }

    /**
     * Retrieves the current MQTT preference data as a single snapshot.
     *
     * @return the current [MqttDataProto.MqttProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): MqttDataProto.MqttProtoModel? = preference.data.firstOrNull()

    /**
     * Updates the MQTT preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges all MQTT connection fields (ip, port, clientId, username, password,
     * enabled, friendlyName) into the existing stored model via the builder.
     *
     * @param value the new [MqttDataProto.MqttProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: MqttDataProto.MqttProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
                // Reset to default instance when null is passed
                MqttDataProto.MqttProtoModel.getDefaultInstance()
            } else {
                preference
                    .toBuilder()
                    .setIp(value.ip)
                    .setPort(value.port)
                    .setClientId(value.clientId)
                    .setUsername(value.username)
                    .setPassword(value.password)
                    .setEnabled(value.enabled)
                    .setFriendlyName(value.friendlyName)
                    .build()
            }
        }
    }
}
