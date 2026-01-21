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

@Single
internal class MqttPreferenceStorage(
    @Named("mqttDataStore")
    private val preference: DataStore<MqttDataProto.MqttProtoModel>,
) : BasePreferenceStorage<MqttDataProto.MqttProtoModel> {
    override fun subscribeToData(): Flow<MqttDataProto.MqttProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(MqttDataProto.MqttProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun getData(): MqttDataProto.MqttProtoModel? = preference.data.firstOrNull()

    override suspend fun updateData(value: MqttDataProto.MqttProtoModel?) {
        preference.updateData { preference ->
            if (value == null) {
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
