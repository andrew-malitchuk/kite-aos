package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.WebEngineDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [WebEngineDataProto.WebEngineProtoModel].
 *
 * Serializes and deserializes browser engine preference data using the Protocol Buffers
 * binary format. The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * @see WebEngineDataProto.WebEngineProtoModel
 * @see data.preferences.impl.core.mapper.WebEngineProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.WebEnginePreferenceStorage
 * @since 0.0.4
 */
internal class WebEngineProtoSerializer : Serializer<WebEngineDataProto.WebEngineProtoModel> {

    override suspend fun readFrom(input: InputStream): WebEngineDataProto.WebEngineProtoModel {
        try {
            return WebEngineDataProto.WebEngineProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: WebEngineDataProto.WebEngineProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: WebEngineDataProto.WebEngineProtoModel =
        WebEngineDataProto.WebEngineProtoModel.getDefaultInstance()
}
