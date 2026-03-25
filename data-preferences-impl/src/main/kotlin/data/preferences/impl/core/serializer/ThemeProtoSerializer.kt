package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.ThemeDataProto
import java.io.InputStream
import java.io.OutputStream

internal class ThemeProtoSerializer : Serializer<ThemeDataProto.ThemeProtoModel> {
    override suspend fun readFrom(input: InputStream): ThemeDataProto.ThemeProtoModel {
        try {
            return ThemeDataProto.ThemeProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ThemeDataProto.ThemeProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: ThemeDataProto.ThemeProtoModel =
        ThemeDataProto.ThemeProtoModel.getDefaultInstance()
}
