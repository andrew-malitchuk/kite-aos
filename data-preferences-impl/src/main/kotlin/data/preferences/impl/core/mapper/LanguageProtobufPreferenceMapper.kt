package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.LanguagePreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.LanguagePreferenceProto

/**
 * Bidirectional mapper between [LanguagePreferenceProto] and [LanguagePreference].
 *
 * Handles conversion of language/locale configuration between the Protobuf serialization format
 * used by Proto DataStore and the preference resource exposed to the data layer API.
 *
 * Null locale codes are stored as empty strings in Protobuf. When reading back, blank strings
 * are converted to `null` to represent the "no explicit locale selected" state.
 *
 * @see LanguagePreference
 * @see LanguagePreferenceProto
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.LanguagePreferenceSourceImpl
 * @since 0.0.1
 */
internal object LanguageProtobufPreferenceMapper :
    ProtobufPreferenceMapper<LanguagePreferenceProto, LanguagePreference> {

    /** Converts a [LanguagePreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<LanguagePreference, LanguagePreferenceProto> =
        Mapper { input ->
            LanguagePreferenceProto.newBuilder()
                // Default to empty string for null locale since Protobuf does not support nullable strings
                .setLocaleCode(input.localeCode ?: "")
                .build()
        }

    /** Converts a Protobuf [LanguagePreferenceProto] back to a [LanguagePreference]. */
    override val toPreference: Mapper<LanguagePreferenceProto, LanguagePreference> =
        Mapper { input ->
            LanguagePreference(
                // Treat blank Protobuf strings as null to represent "no locale selected"
                localeCode = input.localeCode.ifBlank { null },
            )
        }
}
