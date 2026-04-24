package data.preferences.impl.source.storage.base

import kotlinx.coroutines.flow.Flow

/**
 * A base interface for managing typed preference data operations backed by Proto DataStore.
 *
 * Implementations of this interface wrap an `androidx.datastore.core.DataStore` instance and
 * provide reactive observation, single-shot retrieval, and update capabilities for a specific
 * Protobuf model type.
 *
 * @param T the Protobuf-generated model type managed by this storage.
 * @see data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
 * @see data.preferences.impl.core.serializer
 * @since 0.0.1
 */
internal interface BasePreferenceStorage<T> {

    /**
     * Subscribes to the preference data changes.
     *
     * The returned flow emits a new value whenever the underlying DataStore is updated.
     * In case of an [java.io.IOException], implementations should emit the default Protobuf
     * instance rather than propagating the error.
     *
     * @return a [Flow] that emits the current preference data whenever it changes,
     *         or `null` if no data is available.
     */
    fun subscribeToData(): Flow<T?>

    /**
     * Updates the preference data with the given value.
     *
     * When [value] is `null`, the stored data is reset to the Protobuf default instance.
     *
     * @param value the new value to persist, or `null` to reset to defaults.
     */
    suspend fun updateData(value: T?)

    /**
     * Retrieves the current preference data as a single snapshot.
     *
     * @return the current preference data, or `null` if no data is available.
     */
    suspend fun getData(): T?
}
