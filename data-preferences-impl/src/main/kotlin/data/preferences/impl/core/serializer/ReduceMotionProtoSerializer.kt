package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.ReduceMotionDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [ReduceMotionDataProto.ReduceMotionProtoModel].
 *
 * @see ReduceMotionDataProto.ReduceMotionProtoModel
 * @since 0.0.6
 */
internal class ReduceMotionProtoSerializer :
    Serializer<ReduceMotionDataProto.ReduceMotionProtoModel> {

    override suspend fun readFrom(input: InputStream): ReduceMotionDataProto.ReduceMotionProtoModel {
        try {
            return ReduceMotionDataProto.ReduceMotionProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ReduceMotionDataProto.ReduceMotionProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: ReduceMotionDataProto.ReduceMotionProtoModel =
        ReduceMotionDataProto.ReduceMotionProtoModel.getDefaultInstance()
}
