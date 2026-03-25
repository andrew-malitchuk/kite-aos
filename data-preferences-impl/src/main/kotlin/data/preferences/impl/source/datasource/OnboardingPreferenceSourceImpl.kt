package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.OnboardingPreferenceSource
import data.preferences.api.source.resource.OnboardingPreference
import data.preferences.impl.core.mapper.OnboardingProtobufPreferenceMapper
import data.preferences.impl.proto.OnboardingDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.OnboardingPreferenceStorage
import org.koin.core.annotation.Single

@Single(binds = [OnboardingPreferenceSource::class])
internal class OnboardingPreferenceSourceImpl(
    storage: OnboardingPreferenceStorage,
) : BasePreferenceSourceImpl<OnboardingDataProto.OnboardingProtoModel, OnboardingPreference>(
    storage = storage,
    mapper = OnboardingProtobufPreferenceMapper,
),
    OnboardingPreferenceSource
