package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.DashboardDataProto
import java.io.InputStream
import java.io.OutputStream

internal class DashboardProtoSerializer : Serializer<DashboardDataProto.DashboardProtoModel> {
    override suspend fun readFrom(input: InputStream): DashboardDataProto.DashboardProtoModel {
        try {
            return DashboardDataProto.DashboardProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: DashboardDataProto.DashboardProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: DashboardDataProto.DashboardProtoModel =
        DashboardDataProto.DashboardProtoModel.getDefaultInstance()
}
