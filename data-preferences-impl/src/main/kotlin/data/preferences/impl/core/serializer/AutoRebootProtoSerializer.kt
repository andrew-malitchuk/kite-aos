package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.AutoRebootDataProto
import java.io.InputStream
import java.io.OutputStream

internal class AutoRebootProtoSerializer : Serializer<AutoRebootDataProto.AutoRebootProtoModel> {

    override suspend fun readFrom(input: InputStream): AutoRebootDataProto.AutoRebootProtoModel {
        try {
            return AutoRebootDataProto.AutoRebootProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AutoRebootDataProto.AutoRebootProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: AutoRebootDataProto.AutoRebootProtoModel =
        AutoRebootDataProto.AutoRebootProtoModel.getDefaultInstance()
}
