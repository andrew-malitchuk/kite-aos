package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.ReduceMotionDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [ReduceMotionDataProto.ReduceMotionProtoModel].
 *
 * @see ReduceMotionDataProto.ReduceMotionProtoModel
 * @since 0.0.6
 */
internal class ReduceMotionProtoSerializer :
    Serializer<ReduceMotionDataProto.ReduceMotionProtoModel> {

    /**
     * Reads and parses a [ReduceMotionDataProto.ReduceMotionProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [ReduceMotionDataProto.ReduceMotionProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): ReduceMotionDataProto.ReduceMotionProtoModel {
        try {
            return ReduceMotionDataProto.ReduceMotionProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [ReduceMotionDataProto.ReduceMotionProtoModel] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: ReduceMotionDataProto.ReduceMotionProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: ReduceMotionDataProto.ReduceMotionProtoModel =
        ReduceMotionDataProto.ReduceMotionProtoModel.getDefaultInstance()
}
