package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.AutoRebootDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [AutoRebootDataProto.AutoRebootProtoModel].
 *
 * Serializes and deserializes scheduled auto-reboot preference data using the Protocol Buffers
 * binary format. The serialized data is persisted to a `.pb` file managed by DataStore.
 *
 * On read, if the stored bytes cannot be parsed as a valid Protobuf message, a
 * [CorruptionException] is thrown so that DataStore can handle the corruption appropriately.
 *
 * @see AutoRebootDataProto.AutoRebootProtoModel
 * @see data.preferences.impl.core.mapper.AutoRebootProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.AutoRebootPreferenceStorage
 * @since 0.0.1
 */
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
