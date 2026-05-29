package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.ScreensaverDataProto
import java.io.InputStream
import java.io.OutputStream

internal class ScreensaverProtoSerializer : Serializer<ScreensaverDataProto.ScreensaverProtoModel> {

    override suspend fun readFrom(input: InputStream): ScreensaverDataProto.ScreensaverProtoModel {
        try {
            return ScreensaverDataProto.ScreensaverProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ScreensaverDataProto.ScreensaverProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: ScreensaverDataProto.ScreensaverProtoModel =
        ScreensaverDataProto.ScreensaverProtoModel.getDefaultInstance()
}
