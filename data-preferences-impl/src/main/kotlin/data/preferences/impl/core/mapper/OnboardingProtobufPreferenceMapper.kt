package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.OnboardingPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.OnboardingDataProto

internal object OnboardingProtobufPreferenceMapper :
    ProtobufPreferenceMapper<OnboardingDataProto.OnboardingProtoModel, OnboardingPreference> {
    override val toProtobuf: Mapper<OnboardingPreference, OnboardingDataProto.OnboardingProtoModel> =
        Mapper { input ->
            OnboardingDataProto.OnboardingProtoModel.newBuilder()
                .setIsCompleted(input.isCompleted ?: false)
                .build()
        }

    override val toPreference: Mapper<OnboardingDataProto.OnboardingProtoModel, OnboardingPreference> =
        Mapper { input ->
            OnboardingPreference(
                isCompleted = input.isCompleted,
            )
        }
}
