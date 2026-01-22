package data.preferences.impl.source.storage.base

import kotlinx.coroutines.flow.Flow

/**
 * A base interface for managing preference data operations.
 *
 * This interface defines methods for subscribing to data changes, updating data, and retrieving data
 * for preference storage.
 *
 * @param T The type of data being managed by the DAO.
 */
internal interface BasePreferenceStorage<T> {
    /**
     * Subscribes to the preference data changes.
     *
     * @return A [kotlinx.coroutines.flow.Flow] that emits the current preference data whenever it changes.
     */
    fun subscribeToData(): Flow<T?>

    /**
     * Updates the preference data with the given value.
     *
     * @param value The new value to update the preference data with. Can be `null`.
     */
    suspend fun updateData(value: T?)

    /**
     * Retrieves the current preference data.
     *
     * @return The current preference data, or `null` if no data is available.
     */
    suspend fun getData(): T?
}