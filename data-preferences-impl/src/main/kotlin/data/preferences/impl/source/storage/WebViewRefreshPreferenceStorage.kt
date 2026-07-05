package data.preferences.impl.source.storage

import android.util.Log
import androidx.datastore.core.DataStore
import data.preferences.impl.proto.WebViewRefreshDataProto
import data.preferences.impl.source.storage.base.BasePreferenceStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException

/**
 * Proto DataStore-backed storage for WebView refresh preference data.
 *
 * @param preference the [DataStore] instance injected by name.
 * @see BasePreferenceStorage
 * @since 0.0.6
 */
@Single
internal class WebViewRefreshPreferenceStorage(
    @Named("webViewRefreshDataStore") private val preference: DataStore<WebViewRefreshDataProto.WebViewRefreshProtoModel>,
) : BasePreferenceStorage<WebViewRefreshDataProto.WebViewRefreshProtoModel> {

    /**
     * Subscribes to WebView refresh preference data changes.
     *
     * On [IOException], logs the error and emits the default Protobuf instance to allow
     * graceful recovery rather than crashing.
     *
     * @return a [Flow] emitting the current [WebViewRefreshDataProto.WebViewRefreshProtoModel].
     */
    override fun subscribeToData(): Flow<WebViewRefreshDataProto.WebViewRefreshProtoModel?> =
        preference.data.catch { exception ->
            if (exception is IOException) {
                Log.e("Error", exception.message.toString())
                emit(WebViewRefreshDataProto.WebViewRefreshProtoModel.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Retrieves the current WebView refresh preference data as a single snapshot.
     *
     * @return the current [WebViewRefreshDataProto.WebViewRefreshProtoModel], or `null` if unavailable.
     */
    override suspend fun getData(): WebViewRefreshDataProto.WebViewRefreshProtoModel? =
        preference.data.firstOrNull()

    /**
     * Updates the WebView refresh preference data in DataStore.
     *
     * If [value] is `null`, resets the stored data to the default Protobuf instance.
     * Otherwise, merges the enabled flag and interval into the existing stored model via the builder.
     *
     * @param value the new [WebViewRefreshDataProto.WebViewRefreshProtoModel] to persist, or `null` to reset.
     */
    override suspend fun updateData(value: WebViewRefreshDataProto.WebViewRefreshProtoModel?) {
        preference.updateData { current ->
            if (value == null) {
                WebViewRefreshDataProto.WebViewRefreshProtoModel.getDefaultInstance()
            } else {
                current.toBuilder()
                    .setEnabled(value.enabled)
                    .setIntervalSeconds(value.intervalSeconds)
                    .build()
            }
        }
    }
}
