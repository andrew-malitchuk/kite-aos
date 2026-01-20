package data.preferences.impl.core.serializer

import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.LanguagePreferenceProto
import java.io.InputStream
import java.io.OutputStream

internal class LanguageProtoSerializer : Serializer<LanguagePreferenceProto> {
    override val defaultValue: LanguagePreferenceProto = LanguagePreferenceProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LanguagePreferenceProto {
        return try {
            LanguagePreferenceProto.parseFrom(input)
        } catch (_: InvalidProtocolBufferException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: LanguagePreferenceProto, output: OutputStream) {
        t.writeTo(output)
    }
}
