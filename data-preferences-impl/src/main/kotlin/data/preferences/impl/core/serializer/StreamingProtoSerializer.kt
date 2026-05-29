package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.StreamingDataProto
import java.io.InputStream
import java.io.OutputStream

internal class StreamingProtoSerializer : Serializer<StreamingDataProto.StreamingProtoModel> {

    override suspend fun readFrom(input: InputStream): StreamingDataProto.StreamingProtoModel {
        try {
            return StreamingDataProto.StreamingProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StreamingDataProto.StreamingProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: StreamingDataProto.StreamingProtoModel =
        StreamingDataProto.StreamingProtoModel.getDefaultInstance()
}
