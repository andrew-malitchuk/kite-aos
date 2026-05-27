package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.DashboardDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [DashboardDataProto.DashboardProtoModel].
 *
 * Serializes and deserializes dashboard preference data using the Protocol Buffers binary format.
 * The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * On read, if the stored bytes cannot be parsed as a valid Protobuf message, a
 * [CorruptionException] is thrown so that DataStore can handle the corruption appropriately.
 *
 * @see DashboardDataProto.DashboardProtoModel
 * @see data.preferences.impl.core.mapper.DashboardProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.DashboardPreferenceStorage
 * @since 0.0.1
 */
internal class DashboardProtoSerializer : Serializer<DashboardDataProto.DashboardProtoModel> {

    /**
     * Reads and parses a [DashboardDataProto.DashboardProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [DashboardDataProto.DashboardProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): DashboardDataProto.DashboardProtoModel {
        try {
            return DashboardDataProto.DashboardProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [DashboardDataProto.DashboardProtoModel] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: DashboardDataProto.DashboardProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: DashboardDataProto.DashboardProtoModel =
        DashboardDataProto.DashboardProtoModel.getDefaultInstance()
}
