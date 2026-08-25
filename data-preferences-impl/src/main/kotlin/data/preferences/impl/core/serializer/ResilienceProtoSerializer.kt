package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.ResilienceDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [ResilienceDataProto.ResilienceProtoModel].
 *
 * @see ResilienceDataProto.ResilienceProtoModel
 * @since 2.2.0
 */
internal class ResilienceProtoSerializer : Serializer<ResilienceDataProto.ResilienceProtoModel> {

    /**
     * Reads and parses a [ResilienceDataProto.ResilienceProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [ResilienceDataProto.ResilienceProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): ResilienceDataProto.ResilienceProtoModel {
        try {
            return ResilienceDataProto.ResilienceProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [ResilienceDataProto.ResilienceProtoModel] to the [output] stream using Protobuf
     * binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: ResilienceDataProto.ResilienceProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: ResilienceDataProto.ResilienceProtoModel =
        ResilienceDataProto.ResilienceProtoModel.getDefaultInstance()
}
