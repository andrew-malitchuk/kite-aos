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

    /**
     * Reads and parses a [WebEngineDataProto.WebEngineProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [WebEngineDataProto.WebEngineProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): WebEngineDataProto.WebEngineProtoModel {
        try {
            return WebEngineDataProto.WebEngineProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [WebEngineDataProto.WebEngineProtoModel] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: WebEngineDataProto.WebEngineProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: WebEngineDataProto.WebEngineProtoModel =
        WebEngineDataProto.WebEngineProtoModel.getDefaultInstance()
}
