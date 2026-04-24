package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.LanguagePreferenceSource
import data.preferences.api.source.resource.LanguagePreference
import data.preferences.impl.core.mapper.LanguageProtobufPreferenceMapper
import data.preferences.impl.proto.LanguagePreferenceProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.LanguagePreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [LanguagePreferenceSource] backed by Proto DataStore.
 *
 * Delegates persistence to [LanguagePreferenceStorage] and uses
 * [LanguageProtobufPreferenceMapper] for converting between the Protobuf model and the
 * [LanguagePreference] resource.
 *
 * @param languagePreferenceDao the [LanguagePreferenceStorage] providing DataStore access.
 * @see LanguagePreferenceSource
 * @see LanguagePreferenceStorage
 * @see LanguageProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 0.0.1
 */
@Single(binds = [LanguagePreferenceSource::class])
internal class LanguagePreferenceSourceImpl(
    languagePreferenceDao: LanguagePreferenceStorage,
) : BasePreferenceSourceImpl<LanguagePreferenceProto, LanguagePreference>(
    storage = languagePreferenceDao,
    mapper = LanguageProtobufPreferenceMapper,
),
    LanguagePreferenceSource
