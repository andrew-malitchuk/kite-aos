package data.preferences.impl.core.serializer

import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.LanguagePreferenceProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [LanguagePreferenceProto].
 *
 * Serializes and deserializes language/locale preference data using the Protocol Buffers binary
 * format. The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * Unlike other serializers in this module, on read corruption this serializer silently falls
 * back to [defaultValue] instead of throwing a [androidx.datastore.core.CorruptionException].
 * This ensures the application can always start with a valid locale state.
 *
 * @see LanguagePreferenceProto
 * @see data.preferences.impl.core.mapper.LanguageProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.LanguagePreferenceStorage
 * @since 0.0.1
 */
internal class LanguageProtoSerializer : Serializer<LanguagePreferenceProto> {

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: LanguagePreferenceProto = LanguagePreferenceProto.getDefaultInstance()

    /**
     * Reads and parses a [LanguagePreferenceProto] from the given [input] stream.
     *
     * If the stored bytes are corrupted, returns [defaultValue] rather than throwing,
     * ensuring graceful degradation of locale preferences.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [LanguagePreferenceProto], or [defaultValue] on corruption.
     */
    override suspend fun readFrom(input: InputStream): LanguagePreferenceProto {
        return try {
            LanguagePreferenceProto.parseFrom(input)
        } catch (_: InvalidProtocolBufferException) {
            // Gracefully fall back to default on corruption instead of throwing
            defaultValue
        }
    }

    /**
     * Writes the given [LanguagePreferenceProto] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: LanguagePreferenceProto, output: OutputStream) {
        t.writeTo(output)
    }
}
