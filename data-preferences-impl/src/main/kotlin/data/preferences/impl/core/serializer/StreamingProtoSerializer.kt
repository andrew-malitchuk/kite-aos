package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.StreamingDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [StreamingDataProto.StreamingProtoModel].
 *
 * Serializes and deserializes camera streaming preference data using the Protocol Buffers binary
 * format. The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * On read, if the stored bytes cannot be parsed as a valid Protobuf message, a
 * [CorruptionException] is thrown so that DataStore can handle the corruption appropriately.
 *
 * @see StreamingDataProto.StreamingProtoModel
 * @see data.preferences.impl.core.mapper.StreamingProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.StreamingPreferenceStorage
 * @since 0.0.1
 */
internal class StreamingProtoSerializer : Serializer<StreamingDataProto.StreamingProtoModel> {

    /**
     * Reads and parses a [StreamingDataProto.StreamingProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [StreamingDataProto.StreamingProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): StreamingDataProto.StreamingProtoModel {
        try {
            return StreamingDataProto.StreamingProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [StreamingDataProto.StreamingProtoModel] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: StreamingDataProto.StreamingProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: StreamingDataProto.StreamingProtoModel =
        StreamingDataProto.StreamingProtoModel.getDefaultInstance()
}
