package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.LanguagePreference

/**
 * Mapper for converting between [LanguagePreference] and locale code strings.
 *
 * Unlike other preference mappers, this mapper does not implement [ModelResourceMapper]
 * because the domain representation is a simple nullable [String] (locale code) rather
 * than a dedicated [Model] type.
 *
 * @see LanguagePreference
 * @since 0.0.1
 */
internal object LanguagePreferenceMapper {

    /**
     * Converts a [LanguagePreference] to its locale code string representation.
     *
     * @return a [Mapper] that extracts the locale code from the preference.
     */
    val toModel: Mapper<LanguagePreference, String?> =
        Mapper {
            it.localeCode
        }

    /**
     * Converts a nullable locale code string to a [LanguagePreference].
     *
     * @return a [Mapper] that wraps the locale code in a [LanguagePreference].
     */
    val toResource: Mapper<String?, LanguagePreference> =
        Mapper {
            LanguagePreference(it)
        }
}
