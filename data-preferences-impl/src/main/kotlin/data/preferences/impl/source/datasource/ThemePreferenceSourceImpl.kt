package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.ThemePreferenceSource
import data.preferences.api.source.resource.ThemePreference
import data.preferences.impl.core.mapper.ThemeProtobufPreferenceMapper
import data.preferences.impl.proto.ThemeDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.ThemePreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [ThemePreferenceSource] backed by Proto DataStore.
 *
 * Delegates persistence to [ThemePreferenceStorage] and uses
 * [ThemeProtobufPreferenceMapper] for converting between the Protobuf model and the
 * [ThemePreference] resource.
 *
 * @param themePreferenceDao the [ThemePreferenceStorage] providing DataStore access.
 * @see ThemePreferenceSource
 * @see ThemePreferenceStorage
 * @see ThemeProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 0.0.1
 */
@Single(binds = [ThemePreferenceSource::class])
internal class ThemePreferenceSourceImpl(
    themePreferenceDao: ThemePreferenceStorage,
) : BasePreferenceSourceImpl<ThemeDataProto.ThemeProtoModel, ThemePreference>(
    storage = themePreferenceDao,
    mapper = ThemeProtobufPreferenceMapper,
),
    ThemePreferenceSource
