package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.LanguagePreferenceSource
import data.preferences.api.source.resource.LanguagePreference
import data.preferences.impl.core.mapper.LanguageProtobufPreferenceMapper
import data.preferences.impl.proto.LanguagePreferenceProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.LanguagePreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [LanguagePreferenceSource::class])
internal class LanguagePreferenceSourceImpl(
    languagePreferenceDao: LanguagePreferenceStorage,
) : BasePreferenceSourceImpl<LanguagePreferenceProto, LanguagePreference>(
    storage = languagePreferenceDao,
    mapper = LanguageProtobufPreferenceMapper,
), LanguagePreferenceSource
