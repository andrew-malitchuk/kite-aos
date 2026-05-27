package data.preferences.api.source.datasource.base

import data.core.source.resource.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Base interface for preference data sources backed by Proto DataStore.
 *
 * Provides a generic contract for reading, writing, and observing typed preference data.
 * All concrete preference sources extend this interface with a specific [Resource] type.
 *
 * @param PREFERENCE the type of preference resource managed by this data source.
 *
 * @since 0.0.1
 */
public interface PreferenceSource<PREFERENCE : Resource> {

    /**
     * Retrieves the current preference data.
     *
     * @return the current preference value, or `null` if no data has been stored yet.
     */
    public suspend fun getData(): PREFERENCE?

    /**
     * Persists the given preference data.
     *
     * @param data the preference value to store, or `null` to clear the stored data.
     */
    public suspend fun setData(data: PREFERENCE?)

    /**
     * Observes changes to the preference data as a reactive stream.
     *
     * @return a [Flow] emitting the current and subsequent preference values, or `null` when no data is stored.
     */
    public fun observeData(): Flow<PREFERENCE?>
}
