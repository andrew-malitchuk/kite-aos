package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.OnboardingDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [OnboardingDataProto.OnboardingProtoModel].
 *
 * Serializes and deserializes onboarding state preference data using the Protocol Buffers binary
 * format. The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * On read, if the stored bytes cannot be parsed as a valid Protobuf message, a
 * [CorruptionException] is thrown so that DataStore can handle the corruption appropriately.
 *
 * @see OnboardingDataProto.OnboardingProtoModel
 * @see data.preferences.impl.core.mapper.OnboardingProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.OnboardingPreferenceStorage
 * @since 0.0.1
 */
internal class OnboardingProtoSerializer : Serializer<OnboardingDataProto.OnboardingProtoModel> {

    /**
     * Reads and parses an [OnboardingDataProto.OnboardingProtoModel] from the given [input] stream.
     *
     * @param input the [InputStream] containing the serialized Protobuf bytes.
     * @return the deserialized [OnboardingDataProto.OnboardingProtoModel].
     * @throws CorruptionException if the input bytes are not a valid Protobuf message.
     */
    override suspend fun readFrom(input: InputStream): OnboardingDataProto.OnboardingProtoModel {
        try {
            return OnboardingDataProto.OnboardingProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Writes the given [OnboardingDataProto.OnboardingProtoModel] to the [output] stream
     * using Protobuf binary encoding.
     *
     * @param t the model instance to serialize.
     * @param output the [OutputStream] to write the serialized bytes to.
     */
    override suspend fun writeTo(t: OnboardingDataProto.OnboardingProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    /**
     * The default value returned when no data has been persisted yet.
     * Uses the Protobuf-generated default instance with all fields at their zero values.
     */
    override val defaultValue: OnboardingDataProto.OnboardingProtoModel =
        OnboardingDataProto.OnboardingProtoModel.getDefaultInstance()
}
