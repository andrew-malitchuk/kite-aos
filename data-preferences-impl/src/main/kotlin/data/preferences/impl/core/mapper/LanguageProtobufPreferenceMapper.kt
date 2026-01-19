package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.LanguagePreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.LanguagePreferenceProto

internal object LanguageProtobufPreferenceMapper :
    ProtobufPreferenceMapper<LanguagePreferenceProto, LanguagePreference> {
    override val toProtobuf: Mapper<LanguagePreference, LanguagePreferenceProto> =
        Mapper { input ->
            LanguagePreferenceProto.newBuilder()
                .setLocaleCode(input.localeCode ?: "")
                .build()
        }

    override val toPreference: Mapper<LanguagePreferenceProto, LanguagePreference> =
        Mapper { input ->
            LanguagePreference(
                localeCode = input.localeCode.ifBlank { null }
            )
        }
}
