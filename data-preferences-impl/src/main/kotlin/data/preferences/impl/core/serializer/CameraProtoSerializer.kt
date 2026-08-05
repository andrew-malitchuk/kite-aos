package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.CameraDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [CameraDataProto.CameraProtoModel].
 *
 * Serializes and deserializes the camera-source preference using the Protocol Buffers binary
 * format. On read, unparsable bytes raise a [CorruptionException] so DataStore can recover.
 *
 * @see CameraDataProto.CameraProtoModel
 * @see data.preferences.impl.core.mapper.CameraProtobufPreferenceMapper
 * @see data.preferences.impl.source.storage.CameraPreferenceStorage
 * @since 1.4.0
 */
internal class CameraProtoSerializer : Serializer<CameraDataProto.CameraProtoModel> {

    override suspend fun readFrom(input: InputStream): CameraDataProto.CameraProtoModel {
        try {
            return CameraDataProto.CameraProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: CameraDataProto.CameraProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: CameraDataProto.CameraProtoModel =
        CameraDataProto.CameraProtoModel.getDefaultInstance()
}
