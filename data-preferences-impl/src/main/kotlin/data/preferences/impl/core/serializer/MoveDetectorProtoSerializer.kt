package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.MoveDetectorDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [MoveDetectorDataProto.MoveDetectorProtoModel].
 *
 * Serializes and deserializes motion detector preference data using the Protocol Buffers binary
 * format. The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * On read, if the stored bytes cannot be parsed as a valid Protobuf message, a
 * [CorruptionException] is thrown so that DataStore can handle the corruption appropriately.
 *
 * @see MoveDetectorDataProto.MoveDetectorProtoModel
 * @see data.preferences.impl.core.mapper.MoveDetectorProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.MoveDetectorPreferenceStorage
 * @since 0.0.1
 */
internal class MoveDetectorProtoSerializer :
    Serializer<MoveDetectorDataProto.MoveDetectorProtoModel> {

    /**
     * Reads and parses a [MoveDetectorDataProto.MoveDetectorProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [MoveDetectorDataProto.MoveDetectorProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): MoveDetectorDataProto.MoveDetectorProtoModel {
        try {
            return MoveDetectorDataProto.MoveDetectorProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [MoveDetectorDataProto.MoveDetectorProtoModel] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: MoveDetectorDataProto.MoveDetectorProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: MoveDetectorDataProto.MoveDetectorProtoModel =
        MoveDetectorDataProto.MoveDetectorProtoModel.getDefaultInstance()
}
