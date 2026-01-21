package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.ThemePreferenceSource
import data.preferences.api.source.resource.ThemePreference
import data.preferences.impl.core.mapper.ThemeProtobufPreferenceMapper
import data.preferences.impl.proto.ThemeDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.ThemePreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [ThemePreferenceSource::class])
internal class ThemePreferenceSourceImpl(
    themePreferenceDao: ThemePreferenceStorage,
) : BasePreferenceSourceImpl<ThemeDataProto.ThemeProtoModel, ThemePreference>(
    storage = themePreferenceDao,
    mapper = ThemeProtobufPreferenceMapper,
), ThemePreferenceSource