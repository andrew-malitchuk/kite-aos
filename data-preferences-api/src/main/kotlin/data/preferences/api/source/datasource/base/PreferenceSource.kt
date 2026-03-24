package data.preferences.api.source.datasource.base

import data.core.source.resource.Resource
import kotlinx.coroutines.flow.Flow

public interface PreferenceSource<PREFERENCE : Resource> {
    public suspend fun getData(): PREFERENCE?

    public suspend fun setData(data: PREFERENCE?)

    public fun observeData(): Flow<PREFERENCE?>
}
