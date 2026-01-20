package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.DockDataProto
import java.io.InputStream
import java.io.OutputStream

internal class DockProtoSerializer : Serializer<DockDataProto.DockProtoModel> {

    override suspend fun readFrom(input: InputStream): DockDataProto.DockProtoModel {
        try {
            return DockDataProto.DockProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: DockDataProto.DockProtoModel,
        output: OutputStream,
    ) {
        t.writeTo(output)
    }

    override val defaultValue: DockDataProto.DockProtoModel =
        DockDataProto.DockProtoModel.getDefaultInstance()
}
