package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.OnboardingDataProto
import java.io.InputStream
import java.io.OutputStream

internal class OnboardingProtoSerializer : Serializer<OnboardingDataProto.OnboardingProtoModel> {
    override suspend fun readFrom(input: InputStream): OnboardingDataProto.OnboardingProtoModel {
        try {
            return OnboardingDataProto.OnboardingProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: OnboardingDataProto.OnboardingProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: OnboardingDataProto.OnboardingProtoModel =
        OnboardingDataProto.OnboardingProtoModel.getDefaultInstance()
}
