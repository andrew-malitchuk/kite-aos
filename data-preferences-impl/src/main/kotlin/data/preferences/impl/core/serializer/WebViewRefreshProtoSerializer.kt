package data.preferences.impl.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import data.preferences.impl.proto.WebViewRefreshDataProto
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto DataStore [Serializer] for [WebViewRefreshDataProto.WebViewRefreshProtoModel].
 *
 * @see WebViewRefreshDataProto.WebViewRefreshProtoModel
 * @since 0.0.6
 */
internal class WebViewRefreshProtoSerializer :
    Serializer<WebViewRefreshDataProto.WebViewRefreshProtoModel> {

    override suspend fun readFrom(input: InputStream): WebViewRefreshDataProto.WebViewRefreshProtoModel {
        try {
            return WebViewRefreshDataProto.WebViewRefreshProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: WebViewRefreshDataProto.WebViewRefreshProtoModel, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: WebViewRefreshDataProto.WebViewRefreshProtoModel =
        WebViewRefreshDataProto.WebViewRefreshProtoModel.getDefaultInstance()
}
