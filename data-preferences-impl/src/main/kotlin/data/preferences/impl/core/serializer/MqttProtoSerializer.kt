package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.MqttDataProto
import java.io.InputStream
import java.io.OutputStream

internal class MqttProtoSerializer : Serializer<MqttDataProto.MqttProtoModel> {
    override suspend fun readFrom(input: InputStream): MqttDataProto.MqttProtoModel {
        try {
            return MqttDataProto.MqttProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: MqttDataProto.MqttProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: MqttDataProto.MqttProtoModel =
        MqttDataProto.MqttProtoModel.getDefaultInstance()
}
