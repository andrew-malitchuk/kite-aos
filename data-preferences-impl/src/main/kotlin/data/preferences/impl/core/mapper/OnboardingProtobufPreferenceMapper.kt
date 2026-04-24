package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.OnboardingPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.OnboardingDataProto

/**
 * Bidirectional mapper between [OnboardingDataProto.OnboardingProtoModel] and [OnboardingPreference].
 *
 * Handles conversion of the onboarding completion state between the Protobuf serialization
 * format used by Proto DataStore and the preference resource exposed to the data layer API.
 *
 * A null `isCompleted` value defaults to `false` when writing to Protobuf.
 *
 * @see OnboardingPreference
 * @see OnboardingDataProto.OnboardingProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.OnboardingPreferenceSourceImpl
 * @since 0.0.1
 */
internal object OnboardingProtobufPreferenceMapper :
    ProtobufPreferenceMapper<OnboardingDataProto.OnboardingProtoModel, OnboardingPreference> {

    /** Converts an [OnboardingPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<OnboardingPreference, OnboardingDataProto.OnboardingProtoModel> =
        Mapper { input ->
            OnboardingDataProto.OnboardingProtoModel.newBuilder()
                // Default null completion state to false
                .setIsCompleted(input.isCompleted ?: false)
                .build()
        }

    /** Converts a Protobuf [OnboardingDataProto.OnboardingProtoModel] back to an [OnboardingPreference]. */
    override val toPreference: Mapper<OnboardingDataProto.OnboardingProtoModel, OnboardingPreference> =
        Mapper { input ->
            OnboardingPreference(
                isCompleted = input.isCompleted,
            )
        }
}
