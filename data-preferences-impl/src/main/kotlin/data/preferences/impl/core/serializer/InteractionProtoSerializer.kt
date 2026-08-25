package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.InteractionDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [InteractionDataProto.InteractionProtoModel].
 *
 * @see InteractionDataProto.InteractionProtoModel
 * @since 2.2.0
 */
internal class InteractionProtoSerializer : Serializer<InteractionDataProto.InteractionProtoModel> {

    /**
     * Reads and parses a [InteractionDataProto.InteractionProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [InteractionDataProto.InteractionProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): InteractionDataProto.InteractionProtoModel {
        try {
            return InteractionDataProto.InteractionProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [InteractionDataProto.InteractionProtoModel] to the [output] stream using Protobuf
     * binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: InteractionDataProto.InteractionProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: InteractionDataProto.InteractionProtoModel =
        InteractionDataProto.InteractionProtoModel.getDefaultInstance()
}
