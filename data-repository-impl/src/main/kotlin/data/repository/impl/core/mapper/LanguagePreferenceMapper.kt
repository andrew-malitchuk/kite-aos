package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.LanguagePreference

/**
 * Mapper for converting between language preferences and locale codes.
 */
internal object LanguagePreferenceMapper {
    val toModel: Mapper<LanguagePreference, String?> = Mapper {
        it.localeCode
    }

    val toResource: Mapper<String?, LanguagePreference> = Mapper {
        LanguagePreference(it)
    }
}
