package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.ScreensaverDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [ScreensaverDataProto.ScreensaverProtoModel].
 *
 * Serializes and deserializes screensaver preference data using the Protocol Buffers binary format.
 * The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * On read, if the stored bytes cannot be parsed as a valid Protobuf message, a
 * [CorruptionException] is thrown so that DataStore can handle the corruption appropriately.
 *
 * @see ScreensaverDataProto.ScreensaverProtoModel
 * @see data.preferences.impl.core.mapper.ScreensaverProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.ScreensaverPreferenceStorage
 * @since 0.0.1
 */
internal class ScreensaverProtoSerializer : Serializer<ScreensaverDataProto.ScreensaverProtoModel> {

    /**
     * Reads and parses a [ScreensaverDataProto.ScreensaverProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [ScreensaverDataProto.ScreensaverProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): ScreensaverDataProto.ScreensaverProtoModel {
        try {
            return ScreensaverDataProto.ScreensaverProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [ScreensaverDataProto.ScreensaverProtoModel] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: ScreensaverDataProto.ScreensaverProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: ScreensaverDataProto.ScreensaverProtoModel =
        ScreensaverDataProto.ScreensaverProtoModel.getDefaultInstance()
}
