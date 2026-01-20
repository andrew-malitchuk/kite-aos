package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.MoveDetectorDataProto
import java.io.InputStream
import java.io.OutputStream

internal class MoveDetectorProtoSerializer :
    Serializer<MoveDetectorDataProto.MoveDetectorProtoModel> {

    override suspend fun readFrom(input: InputStream): MoveDetectorDataProto.MoveDetectorProtoModel {
        try {
            return MoveDetectorDataProto.MoveDetectorProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: MoveDetectorDataProto.MoveDetectorProtoModel,
        output: OutputStream,
    ) {
        t.writeTo(output)
    }

    override val defaultValue: MoveDetectorDataProto.MoveDetectorProtoModel =
        MoveDetectorDataProto.MoveDetectorProtoModel.getDefaultInstance()
}
