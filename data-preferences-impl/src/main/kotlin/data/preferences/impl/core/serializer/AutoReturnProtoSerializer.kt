package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.AutoReturnDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [AutoReturnDataProto.AutoReturnProtoModel].
 *
 * Serializes and deserializes auto-return preference data using the Protocol Buffers
 * binary format. The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * @see AutoReturnDataProto.AutoReturnProtoModel
 * @see data.preferences.impl.core.mapper.AutoReturnProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.AutoReturnPreferenceStorage
 * @since 0.0.4
 */
internal class AutoReturnProtoSerializer : Serializer<AutoReturnDataProto.AutoReturnProtoModel> {

    override suspend fun readFrom(input: InputStream): AutoReturnDataProto.AutoReturnProtoModel {
        try {
            return AutoReturnDataProto.AutoReturnProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AutoReturnDataProto.AutoReturnProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: AutoReturnDataProto.AutoReturnProtoModel =
        AutoReturnDataProto.AutoReturnProtoModel.getDefaultInstance()
}
